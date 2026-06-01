package com.pgc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenDotaClient {
    private static final Logger log = LoggerFactory.getLogger(OpenDotaClient.class);
    private final String openDotaUrl = "https://api.opendota.com/api/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenDotaClient() {
    }

    public JsonNode getMatch(long matchId) {
        log.info("Obteniendo datos de la partida con id: {}", matchId);
        try {
            return getJson("matches/" + matchId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getLeague(long leagueId) {
        log.info("Obteniendo datos del torneo con id: {}", leagueId);
        try {
            return getJson("leagues/" + leagueId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long[] getLeagueMatchesIds(long leagueId) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl + "leagues/" +  leagueId + "/matchIds"))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(
                    response.body(),
                    long[].class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode getJson(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openDotaUrl + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error HTTP " + response.statusCode() + " para " + endpoint);
        }

        return mapper.readTree(response.body());
    }
}
