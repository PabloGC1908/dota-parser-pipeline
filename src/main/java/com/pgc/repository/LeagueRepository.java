package com.pgc.repository;

import com.pgc.db.DBConnection;
import com.pgc.model.League;

public class LeagueRepository {
    public void saveLeague(League league) {
        DBConnection db = DBConnection.getInstance();

        db.addToBatch(
                "insert_league",
                league.id(),
                league.name(),
                league.ticket(),
                league.banner(),
                league.tier()
        );
    }
}
