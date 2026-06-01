package com.pgc.model;

import java.time.OffsetDateTime;

public record Match(
        long id,
        OffsetDateTime startDateTime,
        OffsetDateTime endDateTime,
        Integer duration,
        Integer direTeamId,
        Integer radiantTeamId,
        Integer firstBloodTime,
        Boolean didRadiantWin,
        Integer patch,
        String replayUrl,
        long leagueId
) {
}
