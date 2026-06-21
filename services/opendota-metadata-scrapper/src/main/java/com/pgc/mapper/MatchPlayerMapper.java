package com.pgc.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.model.MatchPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchPlayerMapper {
    private static final Logger log = LoggerFactory.getLogger(MatchPlayerMapper.class);

    public List<MatchPlayer> map(JsonNode matchPlayersJson, long matchId) {
        List<MatchPlayer> matchPlayers = new ArrayList<>();
        ObjectMapper matchPlayerMapper = new ObjectMapper();

        log.info("Ingresando info de jugadores de la partida: {}", matchId);

        for (JsonNode matchPlayerJson : matchPlayersJson) {
            MatchPlayer matchPlayer = matchPlayerMapper.convertValue(matchPlayerJson, MatchPlayer.class);

            matchPlayer.setMatchId(matchId);
            matchPlayers.add(matchPlayer);
        }

        return matchPlayers;
    }
}
