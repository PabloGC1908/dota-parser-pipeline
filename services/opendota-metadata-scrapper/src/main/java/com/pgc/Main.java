package com.pgc;

import com.pgc.db.DBConnection;
import com.pgc.service.LeagueService;
import com.pgc.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            log.error("Debes ingresar el id de la liga");
            return;
        }

        DBConnection dbConnection = runDbConnection();

//        Source source = new MappedFileSource(args[0]);
//        SimpleRunner runner = new SimpleRunner(source);

        long leagueId = Long.parseLong(args[0]);

        LeagueService leagueService = new LeagueService();
        MatchService matchService = new MatchService();
        leagueService.insertLeague(leagueId);
        leagueService.insertLeagueMatches(leagueId);
//        MainProcessor processor = new MainProcessor();

//        runner.runWith(
//                processor
//        );

        String matchJson = matchService.getRandomMatch();


        dbConnection.close();

        log.info("Replay procesado correctamente.");
        System.out.println(matchJson);
    }

    private static DBConnection runDbConnection() {
        DBConnection dbConnection = DBConnection.getInstance();
        dbConnection.init();

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
                    radiant_win,
                    league_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """
        );

        dbConnection.registerStatement(
            "insert_match_player",
                """
                INSERT INTO MatchPlayer (
                    match_id,
                    player_id,
                    player_name,
                    hero_id,
                    hero_variant,
                    kills,
                    assists,
                    deaths,
                    last_hits,
                    denies,
                    gold_per_min,
                    xp_per_min,
                    level,
                    net_worth,
                    hero_damage,
                    tower_damage,
                    hero_healing,
                    gold,
                    gold_spent,
                    is_radiant,
                    neutral_kills,
                    tower_kills,
                    courier_kills,
                    lane_kills,
                    hero_kills,
                    observers_kills,
                    sentry_kills,
                    roshan_kills,
                    necronomicon_kills,
                    ancient_kills,
                    lane,
                    lane_role,
                    aghanims_scepter,
                    aghanims_shard,
                    obs_placed,
                    sen_placed,
                    lane_efficiency,
                    lane_efficiency_pct,
                    item_0,
                    item_1,
                    item_2,
                    item_3,
                    item_4,
                    item_5,
                    item_6,
                    backpack_0,
                    backpack_1,
                    backpack_2,
                    item_neutral_1,
                    item_neutral_2
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """
        );


        return dbConnection;
    }
}