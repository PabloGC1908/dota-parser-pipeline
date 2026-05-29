package com.pgc.processor;

import com.pgc.db.DBConnection;
import com.pgc.model.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.event.Insert;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.GameEvent;
import skadistats.clarity.processor.entities.*;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.gameevents.OnGameEvent;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.wire.dota.common.proto.DOTACombatLog;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainProcessor {
    @Insert
    private Entities entities;
    private final Map<Integer, Hero> heroes = new HashMap<>();
    private DBConnection dbConnection;

    private static final int GAME_IN_PROGRESS = 5;
    private static final int GAME_POST_GAME = 6;
    private static final int TICKS_PER_SECOND = 30;
    private int lastProcessedSecond = -1;
    private final int gameStartTick = -1;
    private final int pausedTicksAtStart = 0;
    private int totalTicks = 0;
    private boolean metadataExtracted = false;
    private final DateTimeFormatter GAMETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static final Logger log = LoggerFactory.getLogger(MainProcessor.class);

    public MainProcessor() {
        dbConnection = DBConnection.getInstance();
        dbConnection.init();
    }

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

    //@OnCombatLogEntry
    public void onCombatLogEntry(CombatLogEntry cle) {
        Duration gameTimeMillis = Duration.ofMillis((int) (1000.0f * cle.getTimestamp()));
        LocalTime gameTime = LocalTime.MIDNIGHT.plus(gameTimeMillis);
        String time = "[" + GAMETIME_FORMATTER.format(gameTime) + "]";
        switch (cle.getType()) {
            case DOTA_COMBATLOG_DAMAGE:
                log.info("{} {} hits {}{} for {} damage{}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle),
                        cle.getInflictorName() != null ? " with " + cle.getInflictorName() : "",
                        cle.getValue(),
                        cle.getHealth() != 0 ? String.format(" (%s->%s)", cle.getHealth() + cle.getValue(), cle.getHealth()) : ""
                );
                break;
            case DOTA_COMBATLOG_HEAL:
                log.info("{} {}'s {} heals {} for {} health ({}->{})",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getInflictorName(),
                        getTargetNameCompiled(cle),
                        cle.getValue(),
                        cle.getHealth() - cle.getValue(),
                        cle.getHealth()
                );
                break;
            case DOTA_COMBATLOG_MODIFIER_ADD:
                log.info("{} {} receives {} buff/debuff from {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getInflictorName(),
                        getAttackerNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_MODIFIER_REMOVE:
                log.info("{} {} loses {} buff/debuff",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getInflictorName()
                );
                break;
            case DOTA_COMBATLOG_DEATH:
                log.info("{} {} is killed by {}",
                        time,
                        getTargetNameCompiled(cle),
                        getAttackerNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_ABILITY:
                log.info("{} {} {} ability {} (lvl {}){}{}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.isAbilityToggleOn() || cle.isAbilityToggleOff() ? "toggles" : "casts",
                        cle.getInflictorName(),
                        cle.getAbilityLevel(),
                        cle.isAbilityToggleOn() ? " on" : cle.isAbilityToggleOff() ? " off" : "",
                        cle.getTargetName() != null ? " on " + getTargetNameCompiled(cle) : ""
                );
                break;
            case DOTA_COMBATLOG_ITEM:
                log.info("{} {} uses {}{}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getInflictorName(),
                        cle.getTargetName() != null ? " on " + getTargetNameCompiled(cle) : ""
                );
                break;
            case DOTA_COMBATLOG_LOCATION:
                log.info("{} {} location ({}, {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getLocationX(),
                        cle.getLocationY()
                );
                break;
            case DOTA_COMBATLOG_GOLD:
                log.info("{} {} {} {} gold (reason: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue() < 0 ? "loses" : "receives",
                        Math.abs(cle.getValue()),
                        cle.getGoldReason()
                );
                break;
            case DOTA_COMBATLOG_GAME_STATE:
                log.info("{} game state is now {}",
                        time,
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_XP:
                log.info("{} {} gains {} XP (reason: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue(),
                        cle.getXpReason()
                );
                break;
            case DOTA_COMBATLOG_PURCHASE:
                log.info("{} {} purchases {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValueName()
                );
                break;
            case DOTA_COMBATLOG_BUYBACK:
                log.info("{} player in slot {} buys back",
                        time,
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_ABILITY_TRIGGER:
                log.info("{} {} has ability {} triggered{}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getInflictorName(),
                        cle.getTargetName() != null ? " by " + getTargetNameCompiled(cle) : ""
                );
                break;
            case DOTA_COMBATLOG_PLAYERSTATS:
                log.info("{} {} last_hits: {} networth: {} wards: {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getLastHits(),
                        cle.getNetworth(),
                        cle.getObsWardsPlaced()
                );
                break;
            case DOTA_COMBATLOG_MULTIKILL:
                log.info("{} {} MULTI KILL (x{})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_KILLSTREAK:
                log.info("{} {} is on a {} kill streak",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_TEAM_BUILDING_KILL:
                log.info("{} building {} is destroyed (attacker team: {}, target team: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getAttackerTeam(),
                        cle.getTargetTeam()
                );
                break;
            case DOTA_COMBATLOG_FIRST_BLOOD:
                log.info("{} FIRST BLOOD (team: {}, assists: {})",
                        time,
                        cle.getAttackerTeam(),
                        cle.getAssistPlayers()
                );
                break;
            case DOTA_COMBATLOG_MODIFIER_STACK_EVENT:
                log.info("{} {} {} modifier {} stack count: {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getInflictorName(),
                        cle.hasModifierDuration() ? String.format("(duration: %.1fs)", cle.getModifierDuration()) : "",
                        cle.getStackCount()
                );
                break;
            case DOTA_COMBATLOG_NEUTRAL_CAMP_STACK:
                log.info("{} {} stacks a neutral camp (type: {}, team: {})",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getNeutralCampType(),
                        cle.getNeutralCampTeam()
                );
                break;
            case DOTA_COMBATLOG_PICKUP_RUNE:
                log.info("{} {} picks up rune (type: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getRuneType()
                );
                break;
            case DOTA_COMBATLOG_REVEALED_INVISIBLE:
                log.info("{} {} is revealed (invisible)",
                        time,
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_HERO_SAVED:
                log.info("{} {} saves {}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_MANA_RESTORED:
                log.info("{} {}'s {} restores {} mana to {}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getInflictorName(),
                        cle.getValue(),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_HERO_LEVELUP:
                log.info("{} {} reaches level {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_BOTTLE_HEAL_ALLY:
                log.info("{} {} bottle heals {}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_ENDGAME_STATS:
                log.info("{} endgame stats for {}",
                        time,
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_INTERRUPT_CHANNEL:
                log.info("{} {} interrupts {}'s channel",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_ALLIED_GOLD:
                log.info("{} {} receives {} allied gold",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_AEGIS_TAKEN:
                log.info("{} {} picks up the Aegis",
                        time,
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_MANA_DAMAGE:
                log.info("{} {} burns {} mana from {}{}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getValue(),
                        getTargetNameCompiled(cle),
                        cle.getInflictorName() != null ? " with " + cle.getInflictorName() : ""
                );
                break;
            case DOTA_COMBATLOG_PHYSICAL_DAMAGE_PREVENTED:
                log.info("{} {} prevents {} physical damage to {}",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getValue(),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_UNIT_SUMMONED:
                log.info("{} {} summons {}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_ATTACK_EVADE:
                log.info("{} {} evades attack from {}",
                        time,
                        getTargetNameCompiled(cle),
                        getAttackerNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_TREE_CUT:
                log.info("{} {} cuts a tree at ({}, {})",
                        time,
                        getAttackerNameCompiled(cle),
                        cle.getLocationX(),
                        cle.getLocationY()
                );
                break;
            case DOTA_COMBATLOG_SUCCESSFUL_SCAN:
                log.info("{} successful scan (team: {})",
                        time,
                        cle.getAttackerTeam()
                );
                break;
            case DOTA_COMBATLOG_END_KILLSTREAK:
                log.info("{} {}'s kill streak of {} is ended by {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue(),
                        getAttackerNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_BLOODSTONE_CHARGE:
                log.info("{} {} bloodstone charge count: {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getValue()
                );
                break;
            case DOTA_COMBATLOG_CRITICAL_DAMAGE:
                log.info("{} {} crits {} for {} damage{}",
                        time,
                        getAttackerNameCompiled(cle),
                        getTargetNameCompiled(cle),
                        cle.getValue(),
                        cle.getInflictorName() != null ? " with " + cle.getInflictorName() : ""
                );
                break;
            case DOTA_COMBATLOG_SPELL_ABSORB:
                log.info("{} {} absorbs spell {} from {}",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getInflictorName(),
                        getAttackerNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_UNIT_TELEPORTED:
                log.info("{} {} teleports",
                        time,
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_KILL_EATER_EVENT:
                log.info("{} {} kill eater event (id: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getKillEaterEvent()
                );
                break;
            case DOTA_COMBATLOG_NEUTRAL_ITEM_EARNED:
                log.info("{} {} earns a neutral item",
                        time,
                        getTargetNameCompiled(cle)
                );
                break;
            case DOTA_COMBATLOG_STAT_TRACKER_PLAYER:
                log.info("{} {} stat tracker (id: {})",
                        time,
                        getTargetNameCompiled(cle),
                        cle.getTrackedStatId()
                );
                break;

            default:
                DOTACombatLog.DOTA_COMBATLOG_TYPES type = cle.getType();
                log.info("{} ({}): {}", type.name(), type.getNumber(), cle);
                break;

        }
    }

    @OnGameEvent
    public void onGameEvent(GameEvent event) {
        log.info("{}", event.toString());
    }

    @OnTickEnd
    public void onTickEnd(boolean isFull) {
        Entity rules = entities.getByDtName("CDOTAGamerulesProxy");
        if (rules == null) return;

        Integer gameState = rules.getProperty("m_pGameRules.m_nGameState");

        if (gameState == null) {
            return;
        }

        if (gameState == GAME_POST_GAME && !metadataExtracted) {
            log.info("Partida finalizada. Extrayendo metadata...");

            extractAndSaveMatchMetadata();
            metadataExtracted = true;

            return;
        }

        if (isGamePaused(rules)) {
            return;
        }

        processTick();
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

    public void extractAndSaveMatchMetadata() {
        Entity rules = entities.getByDtName("CDOTAGamerulesProxy");
        if (rules == null) {
            System.err.println("No se pudo encontrar la entidad GameRules.");
            return;
        }

        Number matchIdNum = rules.getProperty("m_pGameRules.m_unMatchID64");
        if (matchIdNum == null) matchIdNum = rules.getProperty("m_pGameRules.m_unMatchID");
        long matchId = (matchIdNum != null) ? matchIdNum.longValue() : 0L;

        // Ganador (2 = Radiant, 3 = Dire)
        Integer winner = rules.getProperty("m_pGameRules.m_nGameWinner");
        Boolean didRadiantWin = null;
        if (winner != null && (winner == 2 || winner == 3)) {
            didRadiantWin = (winner == 2);
        }

        Float startTime = rules.getProperty("m_pGameRules.m_flGameStartTime");
        Float endTime = rules.getProperty("m_pGameRules.m_flGameEndTime");

        Integer durationSeconds = null;
        if (startTime != null && endTime != null && endTime > startTime) {
            durationSeconds = Math.round(endTime - startTime);
        }

        dbConnection.addToBatch(
                "MATCH_INFO",
                matchId,
                didRadiantWin,
                durationSeconds,
                null, // StartDateTime
                null, // EndDateTime
                null, // firstBloodTime
                null, // RadiantTeamId
                null, // DireTeamId
                null  // GameVersionId
        );

        log.info("Datos del Match {} encolados exitosamente.", matchId);
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

    private String compileName(String name, boolean isIllusion) {
        return name != null ? name + (isIllusion ? " (Illusion)" : "") : "UNKNOWN";
    }

    private String getAttackerNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getAttackerName(), cle.isAttackerIllusion());
    }

    private String getTargetNameCompiled(CombatLogEntry cle) {
        return compileName(cle.getTargetName(), cle.isTargetIllusion());
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
