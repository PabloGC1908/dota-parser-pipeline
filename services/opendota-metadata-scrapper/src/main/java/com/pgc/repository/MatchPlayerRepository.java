package com.pgc.repository;

import com.pgc.db.DBConnection;
import com.pgc.model.MatchPlayer;

public class MatchPlayerRepository {
    DBConnection dbConnection = DBConnection.getInstance();

    public void saveMatchPlayer(MatchPlayer player) {
        dbConnection.addToBatch(
                "insert_match_player",
                player.getMatchId(),
                player.getId(),
                player.getPlayerName(),
                player.getHeroId(),
                player.getHeroVariant(),
                player.getKills(),
                player.getAssists(),
                player.getDeaths(),
                player.getLastHits(),
                player.getDenies(),
                player.getGoldPerMin(),
                player.getXpPerMin(),
                player.getLevel(),
                player.getNetWorth(),
                player.getHeroDamage(),
                player.getTowerDamage(),
                player.getHeroHealing(),
                player.getGold(),
                player.getGoldSpent(),
                player.isRadiant(),
                player.getNeutralKills(),
                player.getTowerKills(),
                player.getCourierKills(),
                player.getLaneKills(),
                player.getHeroKills(),
                player.getObserverKills(),
                player.getSentryKills(),
                player.getRoshanKills(),
                player.getNecronomiconKills(),
                player.getAncientKills(),
                player.getLane(),
                player.getLaneRole(),
                player.getAghanimsScepter(),
                player.getAghanimsShard(),
                player.getObsPlaced(),
                player.getSenPlaced(),
                player.getLaneEfficiency(),
                player.getLaneEfficiencyPct(),
                player.getItem0(),
                player.getItem1(),
                player.getItem2(),
                player.getItem3(),
                player.getItem4(),
                player.getItem5(),
                player.getItem6(),
                player.getBackpack0(),
                player.getBackpack1(),
                player.getBackpack2(),
                player.getItemNeutral1(),
                player.getItemNeutral2()
        );
    }
}
