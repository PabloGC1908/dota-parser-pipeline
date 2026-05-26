package com.pgc.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    private Connection connection;
    private final Map<String, PreparedStatement> statements = new HashMap<>();
    private final Map<String, Integer> batchCounters = new HashMap<>();
    private final int BATCH_SIZE = 1000;

    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

    public void init() {
        log.info("Inicializando conexión a base de datos...");
        try {
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new IllegalStateException("Faltan variables de entorno de la base de datos");
            }

            long start = System.currentTimeMillis();
            this.connection = DriverManager.getConnection(
                    dbUrl,
                    dbUser,
                    dbPassword
            );
            this.connection.setAutoCommit(false);
            long elapsed = System.currentTimeMillis() - start;

            log.info("Base de datos conectada exitosamente en {} ms", elapsed);
        } catch (SQLException e) {
            log.error("Error crítico al inicializar la BD", e);
        }
    }

    public void registerStatement(String queryName, String sql) {
        log.info("Registrando query [{}]", queryName);

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            statements.put(queryName, stmt);
            batchCounters.put(queryName, 0);

            log.debug("SQL registrado para [{}]: {}", queryName, sql);
        } catch (SQLException e) {
            log.error("Error en la query [{}]", queryName, e);
        }
    }

    public void addToBatch(String queryName, Object... parameters) {
        PreparedStatement stmt = statements.get(queryName);
        if (stmt == null) {
            log.warn("Intento de uso de query no registrada [{}]", queryName);
            return;
        }

        try {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            stmt.addBatch();

            int currentCount = batchCounters.get(queryName) + 1;
            batchCounters.put(queryName, currentCount);

            if (currentCount % 100 == 0) {
                log.debug("Query [{}] lleva {} registros en batch", queryName, currentCount);
            }


            if (currentCount % BATCH_SIZE == 0) {
                long start = System.currentTimeMillis();
                int[] results = stmt.executeBatch();
                long elapsed = System.currentTimeMillis() - start;

                connection.commit();

                log.info("Batch ejecutado [{}] - {} registros procesados en {} ms", queryName, results.length, elapsed);
            }

        } catch (SQLException e) {
            log.error("Error al agregar datos al batch [{}]. Parámetros: {}", queryName, java.util.Arrays.toString(parameters), e);
        }
    }

    public void close() {
        try {
            for (Map.Entry<String, PreparedStatement> entry : statements.entrySet()) {
                String queryName = entry.getKey();
                PreparedStatement stmt = entry.getValue();
                if (stmt != null) {
                    int pending = batchCounters.getOrDefault(queryName, 0);

                    log.info("Ejecutando batch final [{}] con {} registros", queryName, pending);

                    stmt.executeBatch();
                    stmt.close();

                    log.debug("Statement [{}] cerrado", queryName);
                }
            }
            if (connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
                log.info("Conexión cerrada exitosamente");
            }
        } catch (SQLException e) {
            log.error("Error al cerrar recursos de base de datos", e);

            try {
                if (connection != null) {
                    connection.rollback();

                    log.warn("Rollback ejecutado durante cierre");
                }
            } catch (SQLException rollbackEx) {
                log.error("Error durante rollback final", rollbackEx);
            }
        }
    }
}
