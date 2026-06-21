package com.pgc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MatchPlayer {
    private long matchId;
    @JsonProperty("account_id")
    private long id;
    @JsonProperty("name")
    private String playerName;
    private int playerSlot;
    private int heroId;
    private int heroVariant;
    private int kills;
    private int assists;
    private int deaths;
    private int lastHits;
    private int denies;
    private int goldPerMin;
    private int xpPerMin;
    private int level;
    private int netWorth;
    private int heroDamage;
    private int towerDamage;
    private int heroHealing;
    private int gold;
    private int goldSpent;

    @JsonProperty("isRadiant")
    private boolean isRadiant;

    private int neutralKills;
    private int towerKills;
    private int courierKills;
    private int laneKills;
    private int heroKills;
    private int observerKills;
    private int sentryKills;
    private int roshanKills;
    private int necronomiconKills;
    private int ancientKills;

    private int lane;
    private int laneRole;

    private int aghanimsScepter;
    private int aghanimsShard;

    private int obsPlaced;
    private int senPlaced;

    private float laneEfficiency;
    private int laneEfficiencyPct;

    // items
    @JsonProperty("item_0")
    private int item0;
    @JsonProperty("item_1")
    private int item1;
    @JsonProperty("item_2")
    private int item2;
    @JsonProperty("item_3")
    private int item3;
    @JsonProperty("item_4")
    private int item4;
    @JsonProperty("item_5")
    private int item5;
    @JsonProperty("item_6")
    private int item6;

    @JsonProperty("backpack_0")
    private int backpack0;
    @JsonProperty("backpack_1")
    private int backpack1;
    @JsonProperty("backpack_2")
    private int backpack2;

    @JsonProperty("item_neutral_1")
    private int itemNeutral1;
    @JsonProperty("item_neutral_2")
    private int itemNeutral2;

    public MatchPlayer() {
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerSlot() {
        return playerSlot;
    }

    public void setPlayerSlot(int playerSlot) {
        this.playerSlot = playerSlot;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getHeroVariant() {
        return heroVariant;
    }

    public void setHeroVariant(int heroVariant) {
        this.heroVariant = heroVariant;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getLastHits() {
        return lastHits;
    }

    public void setLastHits(int lastHits) {
        this.lastHits = lastHits;
    }

    public int getDenies() {
        return denies;
    }

    public void setDenies(int denies) {
        this.denies = denies;
    }

    public int getGoldPerMin() {
        return goldPerMin;
    }

    public void setGoldPerMin(int goldPerMin) {
        this.goldPerMin = goldPerMin;
    }

    public int getXpPerMin() {
        return xpPerMin;
    }

    public void setXpPerMin(int xpPerMin) {
        this.xpPerMin = xpPerMin;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(int netWorth) {
        this.netWorth = netWorth;
    }

    public int getHeroDamage() {
        return heroDamage;
    }

    public void setHeroDamage(int heroDamage) {
        this.heroDamage = heroDamage;
    }

    public int getTowerDamage() {
        return towerDamage;
    }

    public void setTowerDamage(int towerDamage) {
        this.towerDamage = towerDamage;
    }

    public int getHeroHealing() {
        return heroHealing;
    }

    public void setHeroHealing(int heroHealing) {
        this.heroHealing = heroHealing;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getGoldSpent() {
        return goldSpent;
    }

    public void setGoldSpent(int goldSpent) {
        this.goldSpent = goldSpent;
    }

    public boolean isRadiant() {
        return isRadiant;
    }

    public void setRadiant(boolean radiant) {
        isRadiant = radiant;
    }

    public int getNeutralKills() {
        return neutralKills;
    }

    public void setNeutralKills(int neutralKills) {
        this.neutralKills = neutralKills;
    }

    public int getTowerKills() {
        return towerKills;
    }

    public void setTowerKills(int towerKills) {
        this.towerKills = towerKills;
    }

    public int getCourierKills() {
        return courierKills;
    }

    public void setCourierKills(int courierKills) {
        this.courierKills = courierKills;
    }

    public int getLaneKills() {
        return laneKills;
    }

    public void setLaneKills(int laneKills) {
        this.laneKills = laneKills;
    }

    public int getHeroKills() {
        return heroKills;
    }

    public void setHeroKills(int heroKills) {
        this.heroKills = heroKills;
    }

    public int getObserverKills() {
        return observerKills;
    }

    public void setObserverKills(int observerKills) {
        this.observerKills = observerKills;
    }

    public int getSentryKills() {
        return sentryKills;
    }

    public void setSentryKills(int sentryKills) {
        this.sentryKills = sentryKills;
    }

    public int getRoshanKills() {
        return roshanKills;
    }

    public void setRoshanKills(int roshanKills) {
        this.roshanKills = roshanKills;
    }

    public int getNecronomiconKills() {
        return necronomiconKills;
    }

    public void setNecronomiconKills(int necronomiconKills) {
        this.necronomiconKills = necronomiconKills;
    }

    public int getAncientKills() {
        return ancientKills;
    }

    public void setAncientKills(int ancientKills) {
        this.ancientKills = ancientKills;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getLaneRole() {
        return laneRole;
    }

    public void setLaneRole(int laneRole) {
        this.laneRole = laneRole;
    }

    public int getAghanimsScepter() {
        return aghanimsScepter;
    }

    public void setAghanimsScepter(int aghanimsScepter) {
        this.aghanimsScepter = aghanimsScepter;
    }

    public int getAghanimsShard() {
        return aghanimsShard;
    }

    public void setAghanimsShard(int aghanimsShard) {
        this.aghanimsShard = aghanimsShard;
    }

    public int getObsPlaced() {
        return obsPlaced;
    }

    public void setObsPlaced(int obsPlaced) {
        this.obsPlaced = obsPlaced;
    }

    public int getSenPlaced() {
        return senPlaced;
    }

    public void setSenPlaced(int senPlaced) {
        this.senPlaced = senPlaced;
    }

    public float getLaneEfficiency() {
        return laneEfficiency;
    }

    public void setLaneEfficiency(float laneEfficiency) {
        this.laneEfficiency = laneEfficiency;
    }

    public int getLaneEfficiencyPct() {
        return laneEfficiencyPct;
    }

    public void setLaneEfficiencyPct(int laneEfficiencyPct) {
        this.laneEfficiencyPct = laneEfficiencyPct;
    }

    public int getItem0() {
        return item0;
    }

    public void setItem0(int item0) {
        this.item0 = item0;
    }

    public int getItem1() {
        return item1;
    }

    public void setItem1(int item1) {
        this.item1 = item1;
    }

    public int getItem2() {
        return item2;
    }

    public void setItem2(int item2) {
        this.item2 = item2;
    }

    public int getItem3() {
        return item3;
    }

    public void setItem3(int item3) {
        this.item3 = item3;
    }

    public int getItem4() {
        return item4;
    }

    public void setItem4(int item4) {
        this.item4 = item4;
    }

    public int getItem5() {
        return item5;
    }

    public void setItem5(int item5) {
        this.item5 = item5;
    }

    public int getItem6() {
        return item6;
    }

    public void setItem6(int item6) {
        this.item6 = item6;
    }

    public int getBackpack0() {
        return backpack0;
    }

    public void setBackpack0(int backpack0) {
        this.backpack0 = backpack0;
    }

    public int getBackpack1() {
        return backpack1;
    }

    public void setBackpack1(int backpack1) {
        this.backpack1 = backpack1;
    }

    public int getBackpack2() {
        return backpack2;
    }

    public void setBackpack2(int backpack2) {
        this.backpack2 = backpack2;
    }

    public int getItemNeutral1() {
        return itemNeutral1;
    }

    public void setItemNeutral1(int itemNeutral1) {
        this.itemNeutral1 = itemNeutral1;
    }

    public int getItemNeutral2() {
        return itemNeutral2;
    }

    public void setItemNeutral2(int itemNeutral2) {
        this.itemNeutral2 = itemNeutral2;
    }
}
