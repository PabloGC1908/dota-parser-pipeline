package com.pgc.processor;

import com.pgc.db.DBConnection;
import com.pgc.model.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.event.Insert;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.*;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HeroProcessor {
    @Insert
    private Entities entities;
    private final Map<Integer, Hero> heroes = new HashMap<>();
    private int lastProcessedSecond = -1;
    private final int gameStartTick = -1;
    private final int pausedTicksAtStart = 0;
    private int totalTicks = 0;
    private static final Logger log = LoggerFactory.getLogger(HeroProcessor.class);

    @OnEntityCreated
    public void onEntityCreated(Entity entity) {

        String dtName = entity.getDtClass().getDtName();

        if (dtName.startsWith("CDOTA_Unit_Hero")) {

            int id = entity.getIndex();

            Hero hero = new Hero(id);

            hero.setName(dtName);

            heroes.put(id, hero);

            log.info("Hero creado: {}", dtName);
        }
    }

//    @OnEntityUpdated
//    public void onEntityUpdated(Entity entity, FieldPath[] updatedPaths, int numUpdatedPaths) {
//
//        if (!entity.getDtClass().getDtName().startsWith("CDOTA_Unit_Hero")) {
//            return;
//        }
//
//        Hero hero = heroes.get(entity.getIndex());
//
//        if (hero == null) {
//            return;
//        }
//
//        Number health = entity.getProperty("m_iHealth");
//        Number x = entity.getProperty("CBodyComponent.m_cellX");
//        Number y = entity.getProperty("CBodyComponent.m_cellY");
//
//        Entity rules = entities.getByDtName("CDOTAGamerulesProxy");
//        if (rules != null) {
//            Float gameTime = rules.getProperty("m_pGameRules.m_fGameTime");
//
//            if (gameTime != null) {
//                int currentSecond = Math.round(gameTime);
//
//                if (currentSecond != lastProcessedSecond) {
//                    lastProcessedSecond = currentSecond;
//
//                    System.out.println(
//                            "Segundo: " + currentSecond +
//                                    ", Hero: " + entity.getDtClass().getDtName() +
//                                    ", HP=" + health // Usamos la variable health que ya extrajimos
//                    );
//                }
//            }
//        }
//    }

    @OnTickEnd
    public void onTickEnd(boolean isFull) {
        Entity rules = entities.getByDtName("CDOTAGamerulesProxy");
        if (rules == null) return;

        Integer gameState = rules.getProperty("m_pGameRules.m_nGameState");

        if (gameState == null || gameState != 5) {
            return;
        }

        // Detecta si el juego está pausado
        Object isPausedObj = rules.getProperty("m_pGameRules.m_bGamePaused");
        boolean isPaused = false;

        if (isPausedObj instanceof Boolean) {
            isPaused = (Boolean) isPausedObj;
        } else if (isPausedObj instanceof Integer) {
            isPaused = ((Integer) isPausedObj) == 1;
        }

        if (isPaused) {
            return;
        }

        totalTicks++;

        // reloj
        int currentSecond = totalTicks / 30;

        if (currentSecond != lastProcessedSecond) {
            lastProcessedSecond = currentSecond;

            processAllHeroes(currentSecond);
        }
    }

    private void processAllHeroes(int second) {
        for (Integer heroId : heroes.keySet()) {
            Entity hero = entities.getByIndex(heroId);

            if (hero == null) continue;

            String name = hero.getDtClass().getDtName();
            if (!name.startsWith("CDOTA_Unit_Hero")) {
                continue;
            }

            // Ilusiones
            Integer replicatingHandle = hero.getProperty("m_hReplicatingOtherHeroModel");

            // Ignoramos los valores conocidos de "Handle Nulo" en Source 2 (2097151, 16777215, y -1)
            if (replicatingHandle != null
                    && replicatingHandle != 2097151
                    && replicatingHandle != 16777215
                    && replicatingHandle != -1) {
                continue;
            }

            Integer health = hero.getProperty("m_iHealth");
            Integer cellX = hero.getProperty("CBodyComponent.m_cellX");
            Integer cellY = hero.getProperty("CBodyComponent.m_cellY");

            log.info("[Segundo {}] {} -> HP: {} | Pos: ({}, {})", second, name, health, cellX, cellY);
        }
    }

//    @OnEntityDeleted
//    public void onEntityDeleted(Entity entity) {
//
//        heroes.remove(entity.getIndex());
//
//        System.out.println(
//                "Hero eliminado: " +
//                        entity.getDtClass().getDtName()
//        );
//    }
}
