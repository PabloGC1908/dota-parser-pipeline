package com.pgc;

import com.pgc.db.DBConnection;
import com.pgc.processor.MainProcessor;
import com.pgc.service.OpenDotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.source.Source;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            log.error("Debes pasar un replay .dem");
            return;
        }

        DBConnection dbConnection = runDbConnection();

//        Source source = new MappedFileSource(args[0]);
//        SimpleRunner runner = new SimpleRunner(source);

        OpenDotaService openDotaService = new OpenDotaService();
        openDotaService.downloadLeague(args[0]);

//        MainProcessor processor = new MainProcessor();

//        runner.runWith(
//                processor
//        );

        dbConnection.close();

        log.info("Replay procesado correctamente.");
    }

    private static DBConnection runDbConnection() {
        DBConnection dbConnection = DBConnection.getInstance();
        dbConnection.init();

        dbConnection.registerStatement(
                "insert_match",
                """
                INSERT INTO Match (
                    id,
                    start_date_time,
                    end_date_time,
                    duration_seconds,
                    dire_team_id,
                    radiant_team_id,
                    replay_url,
                    patch,
                    first_blood_time_seconds,
                    radiant_win
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """
        );

        dbConnection.registerStatement(
                "insert_league",
                """
                INSERT INTO League (
                    id,
                    name,
                    ticket,
                    banner,
                    tier
                )
                VALUES (?, ?, ?, ?, ?)
                """
        );


        return dbConnection;
    }
}