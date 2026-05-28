package com.pgc;

import com.pgc.processor.MainProcessor;
import com.pgc.service.OpenDotaService;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.source.Source;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Debes pasar un replay .dem");
            return;
        }

        Source source = new MappedFileSource(args[0]);
        SimpleRunner runner = new SimpleRunner(source);

        OpenDotaService openDotaService = new OpenDotaService(args[0]);

        openDotaService.downloadMatch(args[0]);

        MainProcessor processor = new MainProcessor();

//        runner.runWith(
//                processor
//        );

        System.out.println("Replay procesado correctamente.");
    }
}