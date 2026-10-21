package com.pgc.processor;

import java.util.Set;

public class BronzeCapturePolicy {

    private static final String PLAYER_RESOURCE = "CDOTA_PlayerResource";
    private static final String DATA_RADIANT = "CDOTA_DataRadiant";
    private static final String DATA_DIRE = "CDOTA_DataDire";
    private static final String HERO_PREFIX = "CDOTA_Unit_Hero_";

    private static final Set<String> PLAYER_RESOURCE_FIELDS = Set.of(
        "m_hSelectedHero",
        "m_nSelectedHeroID",
        "m_nSelectedHeroFacet",
        "m_iKills",
        "m_iAssists",
        "m_iDeaths",
        "m_iStreak",
        "m_iLevel",
        "m_flTeamFightParticipation",
        "m_flLastBuybackTime",
        "m_iBountyRunes",
        "m_iPowerRunes",
        "m_iWaterRunes",
        "m_iFirstBloodClaimed",
        "m_iFirstBloodGiven"
    );

    private static final Set<String> TEAM_DATA_FIELDS = Set.of(
        "m_vecControlledUnits.0000",
        "m_iTotalEarnedGold",
        "m_iReliableGold",
        "m_iUnreliableGold",
        "m_iTotalEarnedXP",
        "m_iNetWorth",
        "m_iLastHitCount",
        "m_iDenyCount",
        "m_iHeroDamage",
        "m_iTowerDamage",
        "m_iTowerKills",
        "m_iRoshanKills",
        "m_iObserverWardsPlaced",
        "m_iSentryWardsPlaced",
        "m_iWardsDestroyed"
    );

    private static final Set<String> HERO_FIELDS = Set.of(
        "m_iCurrentLevel",
        "m_iCurrentXP",
        "m_iHealth",
        "m_iMaxHealth",
        "m_lifeState",

        "CBodyComponent.m_cellX",
        "CBodyComponent.m_cellY",
        "CBodyComponent.m_cellZ",

        "CBodyComponent.m_vecX",
        "CBodyComponent.m_vecY",
        "CBodyComponent.m_vecZ",

        "CBodyComponent.m_angRotation",

        "m_flMana",
        "m_flMaxMana",

        "m_flStrengthTotal",
        "m_flAgilityTotal",
        "m_flIntellectTotal",

        "m_iMoveSpeed",
        "m_flRespawnTime",
        "m_flLastDealtDamageTime"
    );


    public boolean acceptsEntity(String dtName) {

        return PLAYER_RESOURCE.equals(dtName)
                || DATA_RADIANT.equals(dtName)
                || DATA_DIRE.equals(dtName)
                || dtName.startsWith(HERO_PREFIX);
    }


    public boolean acceptsProperty(String dtName, String propertyPath) {
        Set<String> allowedFields;

        if (PLAYER_RESOURCE.equals(dtName)) {
            allowedFields = PLAYER_RESOURCE_FIELDS;

        } else if (
                DATA_RADIANT.equals(dtName)
                || DATA_DIRE.equals(dtName)
        ) {
            allowedFields = TEAM_DATA_FIELDS;

        } else if (dtName.startsWith(HERO_PREFIX)) {
            allowedFields = HERO_FIELDS;

        } else {
            return false;
        }

        return allowedFields.stream().anyMatch(field ->
                propertyPath.equals(field) || propertyPath.endsWith("." + field)
        );
    }
}
