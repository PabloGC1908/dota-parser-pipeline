package com.pgc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.client.OpenDotaClient;
import com.pgc.dto.MatchUrlDto;
import com.pgc.mapper.MatchMapper;
import com.pgc.model.Match;
import com.pgc.repository.MatchRepository;

import java.util.Set;

public class MatchService {
    private final OpenDotaClient openDotaClient;
    private final MatchMapper matchMapper;
    private final MatchRepository matchRepository;

    public MatchService(MatchMapper matchMapper, MatchRepository matchRepository) {
        this.matchMapper = matchMapper;
        this.matchRepository = matchRepository;
        this.openDotaClient = new OpenDotaClient();
    }

    public MatchService() {
        openDotaClient = new OpenDotaClient();
        matchMapper = new MatchMapper();
        matchRepository = new MatchRepository();
    }

    public void insertMatch(long matchId) {
        JsonNode jsonMatch = openDotaClient.getMatch(matchId);

        Match match = matchMapper.map(jsonMatch, matchId);

        matchRepository.saveMatch(match);
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
