package com.pgc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgc.client.OpenDotaClient;
import com.pgc.mapper.LeagueMapper;
import com.pgc.model.League;
import com.pgc.repository.LeagueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class LeagueService {
    private final OpenDotaClient openDotaClient;
    private final LeagueMapper leagueMapper;
    private final LeagueRepository leagueRepository;
    private final MatchService matchService;
    private static final Logger log = LoggerFactory.getLogger(LeagueService.class);

    public LeagueService() {
        this.openDotaClient = new OpenDotaClient();
        this.leagueMapper = new LeagueMapper();
        this.leagueRepository = new LeagueRepository();
        this.matchService = new MatchService();
    }

    public boolean checkLeague(long leagueId) {
        boolean leagueExists = leagueRepository.existLeague(leagueId);

        if (!leagueExists) {
            log.warn("La liga no existe en la base de datos");
            return false;
        }

        log.info("La liga esta cargada en la base de datos");

        return true;
    }

    public boolean checkLeagueMatches(long leagueId, long[] matchIds) {
        int matchIdsDb = matchService.getLeagueMatchesCounts(leagueId);

        log.info("Revisando cantidad de partidas de la liga: {}", leagueId);

        if (matchIdsDb != matchIds.length) {
            log.warn("No estan todas las partidas del torneo en la base de datos");
            return false;
        }

        return true;
    }

    public void insertLeague(long leagueId) {
        if (checkLeague(leagueId)) {
            return;
        }

        JsonNode jsonLeague = openDotaClient.getLeague(leagueId);

        League league = leagueMapper.mapLeague(jsonLeague, leagueId);

        leagueRepository.saveLeague(league);
    }

    public void insertLeagueMatches(long leagueId) {
        long[] openDotaMatchIds = openDotaClient.getLeagueMatchesIds(leagueId);

        if (checkLeagueMatches(leagueId, openDotaMatchIds)) {
            return;
        }

        Set<Long> dbMatchIds = matchService.getMatchesIdsByLeagueId(leagueId);

        for (long matchId : openDotaMatchIds) {
            if (dbMatchIds.contains(matchId)) {
                continue;
            }

            log.info("Partida con id {} no encontrada, insertando partida", matchId);

            matchService.insertMatch(matchId);
        }
    }


}
