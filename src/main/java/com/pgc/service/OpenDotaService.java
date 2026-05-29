package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.db.DBConnection;
import com.pgc.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class OpenDotaService {
    private static final Logger log = LoggerFactory.getLogger(OpenDotaService.class);

    public OpenDotaService() {

    }

    public void downloadMatch(String replayId) throws IOException, InterruptedException {
        String stringMatchId = replayId.substring(8, replayId.indexOf("."));
        long matchId = Long.parseLong(stringMatchId);

        HttpClient client = HttpClient.newHttpClient();
        String openDotaUrl = "https://api.opendota.com/api/matches/";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl +  stringMatchId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        mapMatch(response, matchId);
    }

    public void mapMatch(HttpResponse<String> response, long matchId) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        int duration = json.get("duration").asInt();
        long startTime = json.get("start_time").asLong();
        OffsetDateTime startDateTime = convertUnixToDate(startTime);

        long endTime = startTime + duration;
        OffsetDateTime endDateTime = convertUnixToDate(endTime);

        int direTeamId =  json.get("dire_team_id").asInt();
        int radiantTeamId = json.get("radiant_team_id").asInt();
        String replayUrl = json.get("replay_url").asText();
        int patch = json.get("patch").asInt();
        int firstBloodTime =  json.get("first_blood_time").asInt();
        boolean didRadiantWin = json.get("radiant_win").asBoolean();

        log.info("Extrayendo datos de partida con id {}: {}, {}, {}, {}, {}, {}, {}, {}, {}", matchId, startDateTime, duration, direTeamId, radiantTeamId, replayUrl, patch, firstBloodTime, didRadiantWin, endDateTime);

        Match match = new Match(
            matchId,
            startDateTime,
            endDateTime,
            duration,
            direTeamId,
            radiantTeamId,
            firstBloodTime,
            didRadiantWin,
            patch,
            replayUrl
        );

        saveMatch(match);
    }

    public void saveMatch(Match match) {
        DBConnection dbConnection = DBConnection.getInstance();

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
                match.didRadiantWin()
        );
    }

    public OffsetDateTime convertUnixToDate(long unixDate) {
        Instant instant = Instant.ofEpochSecond(unixDate);

        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
