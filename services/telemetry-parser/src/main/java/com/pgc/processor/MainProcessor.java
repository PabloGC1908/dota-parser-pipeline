package com.pgc.processor;

import com.pgc.db.DBConnection;
import com.pgc.model.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.event.Insert;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;

import java.util.HashMap;
import java.util.Map;

public class MainProcessor {
    @Insert
    private Entities entities;
    private final Map<Integer, Hero> heroes = new HashMap<>();
    private static final int GAME_PRE_GAME = 3;
    private static final int GAME_IN_PROGRESS = 5;
    private static final int GAME_POST_GAME = 6;
    private static final int TICKS_PER_SECOND = 30;
    private int lastProcessedSecond = -1;
    private final int gameStartTick = -1;
    private final int pausedTicksAtStart = 0;
    private int totalTicks = 0;
    private boolean metadataExtracted = false;
    private DBConnection dbConnection;

    private static final Logger log = LoggerFactory.getLogger(MainProcessor.class);

    public MainProcessor() {
        log.info("MainProcessor creado");
        dbConnection = DBConnection.getInstance();
    }

/*    @OnEntityCreated
    public void onEntityCreated(Entity entity) {

        String dtName = entity.getDtClass().getDtName();

        if (dtName.startsWith("CDOTA_Unit_Hero")) {

            int id = entity.getIndex();

            Hero hero = new Hero(id);

            hero.setName(dtName);

            heroes.put(id, hero);

            log.info("Hero creado: {}", dtName);
        }
    }*/

    @OnEntityCreated
    public void onEntityCreated(Entity entity) {
        String dtName = entity.getDtClass().getDtName();

        if (!dtName.equals("CDOTA_DataRadiant") &&
            !dtName.equals("CDOTA_DataDire")) {
            return;
        }
    }

    @OnTickEnd
    public void onTickEnd(Context ctx, boolean synthetic) {
        Entity playerResource = entities.getByDtName("CDOTA_PlayerResource");
        Entity radiantData = entities.getByDtName("CDOTA_DataRadiant");
        Entity direData = entities.getByDtName("CDOTA_DataDire");
        Entity rules = entities.getByDtName("CDOTAGamerulesProxy");

        log.info("ON TICK END - isFull={}", synthetic);

        if (rules == null) return;

        Integer gameState = rules.getProperty("m_pGameRules.m_nGameState");
        if (gameState == null) {
            return;
        }

        if (isGamePaused(rules)) {
            return;
        }

        for (int playerIndex = 0; playerIndex < 10; playerIndex++) {
            // 1. Obtener handle del héroe desde CDOTA_PlayerResource
            String prPath = String.format("m_vecPlayerTeamData.%04d.m_hSelectedHero", playerIndex);

            Integer heroHandle = playerResource.getProperty(prPath);
//            log.info("Handle del heroe: {}", heroHandle);

            // 2. Validar que el handle sea válido (diferente de null y de 16777215 / INVALID_HANDLE)
            if (heroHandle != null && heroHandle != 16777215) {
                // Resolver la entidad del héroe usando el handle
                Entity heroEntity = entities.getByHandle(heroHandle);

                if (heroEntity != null) {
                    String heroName = heroEntity.getDtClass().getDtName(); // Ej: "CDOTA_Hero_Nevermore"
                    log.info("Hero Name: {}", heroName);

                    Integer health = heroEntity.getProperty("m_iHealth");
                    Integer cellX = heroEntity.getProperty("CBodyComponent.m_cellX");
                    Integer cellY = heroEntity.getProperty("CBodyComponent.m_cellY");

                    log.info("HP: {} | Pos: ({}, {})", health, cellX, cellY);

                    // 3. (Opcional) Obtener la unidad controlada desde CDOTA_DataRadiant / CDOTA_DataDire
                    Entity teamData = (playerIndex < 5) ? radiantData : direData;
                    int teamSlot = playerIndex % 5;

                    if (teamData != null) {
                        String teamPath = String.format("m_vecDataTeam.%04d.m_vecControlledUnits.0000", teamSlot);
                        Integer controlledUnitHandle = teamData.getProperty(teamPath);

                        // heroHandle y controlledUnitHandle apuntarán a la misma entidad heroEntity
                    }

                    // Procesar datos del héroe (Posición, HP, Nivel, etc.)
                    // Vector3f location = heroEntity.getProperty("m_vecOrigin");
                }
            }
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

            if (isIllusion(hero)) {
                continue;
            }

            Integer health = hero.getProperty("m_iHealth");
            Integer cellX = hero.getProperty("CBodyComponent.m_cellX");
            Integer cellY = hero.getProperty("CBodyComponent.m_cellY");

            log.info("[Segundo {}] {} -> HP: {} | Pos: ({}, {})", second, name, health, cellX, cellY);
        }
    }

    private boolean isIllusion(Entity hero) {

        Integer replicatingHandle = hero.getProperty("m_hReplicatingOtherHeroModel");

        // Valores conocidos de "Handle Nulo" en Source 2 (2097151, 16777215, y -1)
        return replicatingHandle != null
                && replicatingHandle != 2097151
                && replicatingHandle != 16777215
                && replicatingHandle != -1;
    }

    private void processTick() {
        totalTicks++;
        int currentSecond = totalTicks / TICKS_PER_SECOND;

        if (currentSecond == lastProcessedSecond) {
            return;
        }

        lastProcessedSecond = currentSecond;

        processAllHeroes(currentSecond);
    }

    private boolean isGamePaused(Entity rules) {

        Object pausedObj = rules.getProperty("m_pGameRules.m_bGamePaused");

        if (pausedObj instanceof Boolean) {
            return (Boolean) pausedObj;
        }

        if (pausedObj instanceof Integer) {
            return ((Integer) pausedObj) == 1;
        }

        return false;
    }
}
