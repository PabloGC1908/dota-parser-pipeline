package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenDotaService {
    private final String openDotaUrl = "https://api.opendota.com/api/matches/";
    private String replayId;

    public OpenDotaService(String replayId) {
        this.replayId = replayId;
    }

    public void downloadMatch(String replayId) throws IOException, InterruptedException {
        String stringMatchId = replayId.substring(9, replayId.indexOf("."));
        Integer matchId = Integer.parseInt(stringMatchId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.opendota.com/api/matches/1234567890"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        System.out.println(json);
    }
}
