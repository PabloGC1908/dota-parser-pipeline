package com.pgc.repository;

import com.pgc.db.DBConnection;
import com.pgc.dto.MatchUrlDto;
import com.pgc.model.Match;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MatchRepository {
    DBConnection dbConnection = DBConnection.getInstance();

    public void saveMatch(Match match) {
        dbConnection.addToBatch(
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

    public int countLeagueMatches(long leagueId) {
        String sql =
        """
            SELECT COUNT(*)
            FROM Match
            WHERE league_id = ?
        """;

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, leagueId);

            ResultSet rs = stmt.executeQuery();

            rs.next();

            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Long> getLeagueMatchIds(long leagueId) {
        String sql = """
            SELECT id
            FROM Match
            WHERE league_id = ?
        """;

        Set<Long> matchIds = new HashSet<>();

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setLong(1, leagueId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                matchIds.add(rs.getLong("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return matchIds;
    }


    public MatchUrlDto getRandomMatch() {
        String sql = """
            SELECT TOP 1
                id,
                replay_url
            FROM Match
            ORDER BY NEWID()
            """;

        try (
            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next())
                return new MatchUrlDto(
                        rs.getLong("id"),
                        rs.getString("replay_url")
                );

            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
