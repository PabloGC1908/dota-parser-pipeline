package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgc.mapper.MatchPlayerMapper;
import com.pgc.model.MatchPlayer;
import com.pgc.repository.MatchPlayerRepository;

import java.util.List;

public class MatchPlayerService {
    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchPlayerMapper matchPlayerMapper;

    public MatchPlayerService(){
        this.matchPlayerRepository = new MatchPlayerRepository();
        this.matchPlayerMapper = new MatchPlayerMapper();
    }

    public void insertMatchPlayers(JsonNode jsonMatchPlayers, long matchId){
        List<MatchPlayer> matchPlayers = matchPlayerMapper.map(jsonMatchPlayers, matchId);

        for (MatchPlayer matchPlayer : matchPlayers) {
            matchPlayerRepository.saveMatchPlayer(matchPlayer);
        }
    }
}
