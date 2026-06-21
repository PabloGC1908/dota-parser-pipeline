package com.pgc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.client.OpenDotaClient;
import com.pgc.dto.MatchUrlDto;
import com.pgc.mapper.MatchMapper;
import com.pgc.mapper.MatchPlayerMapper;
import com.pgc.model.Match;
import com.pgc.model.MatchPlayer;
import com.pgc.repository.MatchPlayerRepository;
import com.pgc.repository.MatchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MatchService {
    private final OpenDotaClient openDotaClient;
    private final MatchMapper matchMapper;
    private final MatchRepository matchRepository;
    private final MatchPlayerMapper matchPlayerMapper;
    private final MatchPlayerService matchPlayerService;

    public MatchService(MatchMapper matchMapper, MatchRepository matchRepository, MatchPlayerMapper matchPlayerMapper, MatchPlayerService matchPlayerService) {
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
        this.matchPlayerMapper = matchPlayerMapper;
        this.matchPlayerService = matchPlayerService;
        this.openDotaClient = new OpenDotaClient();
    }

    public MatchService() {
        this.matchPlayerService = new MatchPlayerService();
        this.matchMapper = new MatchMapper();
        this.matchRepository = new MatchRepository();
        this.matchPlayerMapper = new MatchPlayerMapper();
        this.openDotaClient = new OpenDotaClient();
    }

    public void insertMatch(long matchId) {
        JsonNode jsonMatch = openDotaClient.getMatch(matchId);

        Match match = matchMapper.map(jsonMatch, matchId);

        matchRepository.saveMatch(match);
        matchPlayerService.insertMatchPlayers(jsonMatch.get("players"), matchId);
    }

    public int getLeagueMatchesCounts(long leagueId) {
        return matchRepository.countLeagueMatches(leagueId);
    }

    public Set<Long> getMatchesIdsByLeagueId(long leagueId) {
        return matchRepository.getLeagueMatchIds(leagueId);
    }

    public String getRandomMatch() {
        MatchUrlDto matchUrlDto = matchRepository.getRandomMatch();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(matchUrlDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
