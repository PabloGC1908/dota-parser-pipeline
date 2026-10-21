package com.pgc.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public record ClarityEntityChange(
        long matchId,
        int replayTick,
        int entityHandle,
        String dtName,
        OperationState operation,
        ObjectNode properties,
        boolean fullState,
        String parserVersion
) {
}
