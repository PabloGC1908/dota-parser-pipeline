package com.pgc;

import com.pgc.processor.HeroProcessor;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.source.Source;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Debes pasar un replay .dem");
            return;
        }

        Source source = new MappedFileSource(args[0]);

        SimpleRunner runner = new SimpleRunner(source);

        HeroProcessor processor = new HeroProcessor();

        runner.runWith(
                processor
        );

        System.out.println("Replay procesado correctamente.");
    }
}