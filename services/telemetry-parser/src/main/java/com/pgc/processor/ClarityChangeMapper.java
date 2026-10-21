package com.pgc.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pgc.model.ClarityEntityChange;
import com.pgc.model.OperationState;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;

import java.util.Optional;

public class ClarityChangeMapper {
    private final ObjectMapper objectMapper;
    private final BronzeCapturePolicy capturePolicy;
    private final long matchId;
    private final String parserVersion;

    public ClarityChangeMapper(long matchId, String parserVersion, BronzeCapturePolicy capturePolicy) {
        this.matchId = matchId;
        this.parserVersion = parserVersion;
        this.objectMapper = new ObjectMapper();
        this.capturePolicy = capturePolicy;
    }

    public Optional<ClarityEntityChange> mapUpdate(Entity entity, FieldPath[] fieldPaths, int updateCount, int replayTick) {
        String dtName = entity.getDtClass().getDtName();

        // 1. Comprobar si la entidad está permitida.
        if (!capturePolicy.acceptsEntity(dtName)) {
            return Optional.empty();
        }

        ObjectNode properties =
                objectMapper.createObjectNode();

        // 2. Recorrer únicamente los FieldPath válidos.
        int count = Math.min(updateCount, fieldPaths.length);

        for (int i = 0; i < count; i++) {

            FieldPath fieldPath = fieldPaths[i];

            String path = entity.getDtClass()
                    .getNameForFieldPath(fieldPath);

            // 3. Comprobar si la propiedad está permitida.
            if (!capturePolicy.acceptsProperty(dtName, path)) {
                continue;
            }

            Object value =
                    entity.getPropertyForFieldPath(fieldPath);

            if (value == null) {
                properties.putNull(path);
            } else {
                JsonNode jsonValue =
                        objectMapper.valueToTree(value);

                properties.set(path, jsonValue);
            }
        }

        // 4. No insertar eventos vacíos.
        if (properties.isEmpty()) {
            return Optional.empty();
        }

        // 5. Crear el evento Bronze.
        ClarityEntityChange change =
                new ClarityEntityChange(
                        matchId,
                        replayTick,
                        entity.getHandle(),
                        dtName,
                        OperationState.UPDATE,
                        properties,
                        false,
                        parserVersion
                );

        return Optional.of(change);
    }

    public ClarityEntityChange mapCreate(Entity entity, int replayTick, ObjectNode properties) {

        return new ClarityEntityChange(
                matchId,
                replayTick,
                entity.getHandle(),
                entity.getDtClass().getDtName(),
                OperationState.CREATE,
                properties,
                true,
                parserVersion
        );
    }

    public ClarityEntityChange mapDelete(Entity entity, int replayTick) {

        return new ClarityEntityChange(
                matchId,
                replayTick,
                entity.getHandle(),
                entity.getDtClass().getDtName(),
                OperationState.DELETE,
                null,
                false,
                parserVersion
        );
    }
}