package com.pgc.repository;

import com.pgc.db.DBConnection;
import com.pgc.model.League;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeagueRepository {
    private final DBConnection db = DBConnection.getInstance();

    public void saveLeague(League league) {
        db.addToBatch(
                "insert_league",
                league.id(),
                league.name(),
                league.ticket(),
                league.banner(),
                league.tier()
        );
    }

    public boolean existLeague(long leagueId) {
        String sql =
        """
            SELECT 1
            FROM League
            WHERE id = ?
        """;

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, leagueId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
