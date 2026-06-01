package com.pgc.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgc.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MatchMapper {
    private static final Logger log = LoggerFactory.getLogger(MatchMapper.class);

    public Match map(JsonNode jsonMatch, long matchId) {
        int duration = jsonMatch.get("duration").asInt();
        long startTime = jsonMatch.get("start_time").asLong();
        OffsetDateTime startDateTime = convertUnixToDate(startTime);

        long endTime = startTime + duration;
        OffsetDateTime endDateTime = convertUnixToDate(endTime);

        int direTeamId =  jsonMatch.get("dire_team_id").asInt();
        int radiantTeamId = jsonMatch.get("radiant_team_id").asInt();
        String replayUrl = jsonMatch.get("replay_url").asText();
        int patch = jsonMatch.get("patch").asInt();
        int firstBloodTime =  jsonMatch.get("first_blood_time").asInt();
        boolean didRadiantWin = jsonMatch.get("radiant_win").asBoolean();
        long leagueId = jsonMatch.get("leagueid").asLong();

        log.info("Mapeando datos de partida con id {}: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}", matchId, startDateTime, duration, direTeamId, radiantTeamId, replayUrl, patch, firstBloodTime, didRadiantWin, endDateTime, leagueId);

        return new Match(
                matchId,
                startDateTime,
                endDateTime,
                duration,
                direTeamId,
                radiantTeamId,
                firstBloodTime,
                didRadiantWin,
                patch,
                replayUrl,
                leagueId
        );
    }

    public OffsetDateTime convertUnixToDate(long unixDate) {
        Instant instant = Instant.ofEpochSecond(unixDate);

        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
