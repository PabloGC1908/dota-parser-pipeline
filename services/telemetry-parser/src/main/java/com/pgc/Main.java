package com.pgc;


import com.pgc.db.DBConnection;
import com.pgc.processor.BronzeProcessor;
import com.pgc.processor.MainProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.processor.runner.ControllableRunner;
import skadistats.clarity.source.MappedFileSource;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            log.error("Debes ingresar el id de la liga");
            return;
        }

        DBConnection connection = DBConnection.getInstance();
        connection.init();


        new Main().runSeek(args);
    }

    public void runSeek(String[] args) throws IOException, InterruptedException {
        try (MappedFileSource source = new MappedFileSource(args[0])) {
            ControllableRunner runner  = new ControllableRunner(source);

            long matchId = Long.parseLong(args[0].replace("replays/", "").replace(".dem", ""));

            log.info("Empezando extraccion de partida con id: {}", matchId);

            BronzeProcessor processor = new BronzeProcessor(matchId,"1.0");
            try {
                log.info("Iniciando runner");
                runner.runWith(processor);

                while (!runner.isAtEnd())
                    runner.tick();

                log.info("Finalizando runner");
            } finally {
                runner.halt();
                runner.join();
            }
        }
    }
}