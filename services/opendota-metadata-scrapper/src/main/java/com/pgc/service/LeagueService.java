package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgc.client.OpenDotaClient;
import com.pgc.mapper.LeagueMapper;
import com.pgc.model.League;
import com.pgc.repository.LeagueRepository;

public class LeagueService {
    private final OpenDotaClient openDotaClient;
    private final LeagueMapper leagueMapper;
    private final LeagueRepository leagueRepository;
    private final MatchService matchService;

    public LeagueService() {
        this.openDotaClient = new OpenDotaClient();
        this.leagueMapper = new LeagueMapper();
        this.leagueRepository = new LeagueRepository();
        this.matchService = new MatchService();
    }

    public void insertLeague(long leagueId) {
        JsonNode jsonLeague = openDotaClient.getLeague(leagueId);

        League league = leagueMapper.mapLeague(jsonLeague, leagueId);

        leagueRepository.saveLeague(league);
    }

    public void insertLeagueMatches(long leagueId) {
        long[] matchIds = openDotaClient.getLeagueMatchesIds(leagueId);

        for (long matchId : matchIds) {
            matchService.insertMatch(matchId);
        }
    }


}
