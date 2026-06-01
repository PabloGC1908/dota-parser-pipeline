package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.db.DBConnection;
import com.pgc.model.League;
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
    private final String openDotaUrl = "https://api.opendota.com/api/";

    public OpenDotaService() {

    }

    public void downloadMatch(long matchId) throws IOException, InterruptedException {
//        String stringMatchId = replayId.substring(8, replayId.indexOf("."));
//        long matchId = Long.parseLong(stringMatchId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl + "matches/" +  matchId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        mapMatch(json, matchId);
    }

    public void downloadLeague(String textLeague) throws IOException, InterruptedException {
        long leagueId = Long.parseLong(textLeague);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl + "leagues/" +  leagueId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        if (response.statusCode() == 200) {
            mapLeague(json, leagueId);

            long[] matchIds = downloadLeagueMatchesIds(leagueId);

            for (long matchId : matchIds) {
                downloadMatch(matchId);
            }
        }
    }

    public long[] downloadLeagueMatchesIds(long leagueId) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl + "leagues/" +  leagueId + "/matchIds"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                response.body(),
                long[].class
        );
    }

    public void mapMatch(JsonNode jsonMatch, long matchId) {
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

    public void mapLeague(JsonNode jsonLeague, long leagueId) {
        String name = jsonLeague.get("name").asText();
        String ticket = jsonLeague.get("ticket").asText();
        String banner = jsonLeague.get("banner").asText();
        String tier = jsonLeague.get("tier").asText();

        log.info("Extrayendo datos de la liga con id {}: {}, {}, {}, {}", leagueId, name, ticket, banner, tier);

        League league = new League(
                leagueId,
                name,
                ticket,
                banner,
                tier
        );

        saveLeague(league);
    }

    public void saveLeague(League league) {
        DBConnection dbConnection = DBConnection.getInstance();

        dbConnection.addToBatch(
                "insert_league",
                league.id(),
                league.name(),
                league.ticket(),
                league.banner(),
                league.tier()
        );
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
