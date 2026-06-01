package com.pgc.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.pgc.model.League;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeagueMapper {
    private static final Logger log = LoggerFactory.getLogger(LeagueMapper.class);

    public League mapLeague(JsonNode jsonLeague, long leagueId) {
        String name = jsonLeague.get("name").asText();
        String ticket = jsonLeague.get("ticket").asText();
        String banner = jsonLeague.get("banner").asText();
        String tier = jsonLeague.get("tier").asText();

        log.info("Mapeando datos de la liga con id {}: {}, {}, {}, {}", leagueId, name, ticket, banner, tier);

        return new League(
                leagueId,
                name,
                ticket,
                banner,
                tier
        );
    }
}
