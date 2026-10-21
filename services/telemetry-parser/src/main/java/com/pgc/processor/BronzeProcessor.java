package com.pgc.processor;


import com.pgc.db.DBConnection;
import com.pgc.model.ClarityEntityChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.OnEntityDeleted;
import skadistats.clarity.processor.entities.OnEntityUpdated;
import skadistats.clarity.processor.runner.Context;

public class BronzeProcessor {
    private static final Logger log = LoggerFactory.getLogger(BronzeProcessor.class);
    private static final String INSERT_ENTITY_CHANGE = "bronze_clarity_entity_change";
    private final DBConnection dbConnection;
    private final ClarityChangeMapper mapper;
    private final long matchId;

    public BronzeProcessor(long matchId, String parserVersion) {
        BronzeCapturePolicy policy = new BronzeCapturePolicy();
        this.matchId = matchId;
        this.dbConnection = DBConnection.getInstance();
        this.mapper = new ClarityChangeMapper(matchId, parserVersion, policy);

        registerQueries();
    }

    @OnEntityUpdated
    public void onEntityUpdated(Context ctx, Entity entity, FieldPath[] fieldPaths, int updateCount) {
        int currentTick = ctx.getTick();

        mapper.mapUpdate(
                entity,
                fieldPaths,
                updateCount,
                currentTick
        ).ifPresent(this::persist);
    }

    @OnEntityDeleted
    public void onEntityDeleted(Context ctx, Entity entity) {
        int currentTick = ctx.getTick();

        ClarityEntityChange change =
                mapper.mapDelete(
                        entity,
                        currentTick
                );

        persist(change);
    }

    private void persist(ClarityEntityChange change) {
        String properties =
                change.properties() == null
                        ? null
                        : change.properties().toString();

        dbConnection.addToBatch(
                INSERT_ENTITY_CHANGE,
                change.matchId(),
                change.replayTick(),
                change.entityHandle(),
                change.dtName(),
                change.operation().name(),
                properties,
                change.fullState(),
                change.parserVersion()
        );
    }

    private void registerQueries() {

        dbConnection.registerStatement(
                INSERT_ENTITY_CHANGE,
                """
                INSERT INTO sch_rdv.clarity_entity_change (
                    match_id,
                    replay_tick,
                    entity_handle,
                    dt_name,
                    operation,
                    properties,
                    is_full_state,
                    parser_version
                )
                VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?)
                """
        );
    }
}