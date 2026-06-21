package com.pgc;


import com.pgc.db.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.source.Source;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("Debes ingresar el id de la liga");
            return;
        }

        DBConnection connection = DBConnection.getInstance();
        connection.init();

        try {
            Source source = new MappedFileSource(args[0]);
            SimpleRunner runner = new SimpleRunner(source);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}