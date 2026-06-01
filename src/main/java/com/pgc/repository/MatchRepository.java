package com.pgc.repository;

import com.pgc.db.DBConnection;
import com.pgc.model.Match;

public class MatchRepository {
    public void saveMatch(Match match) {
        DBConnection db = DBConnection.getInstance();

        db.addToBatch(
                "insert_match",
                match.id(),
                match.startDateTime(),
                match.endDateTime(),
                match.duration(),
                match.direTeamId(),
                match.radiantTeamId(),
                match.replayUrl(),
                match.patch(),
                match.firstBloodTime(),
                match.didRadiantWin(),
                match.leagueId()
        );
    }
}
