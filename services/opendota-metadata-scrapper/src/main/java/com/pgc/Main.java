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
        log.info("Partida aleatoria: {}", matchJson);
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
                    radiant_win,
                    league_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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