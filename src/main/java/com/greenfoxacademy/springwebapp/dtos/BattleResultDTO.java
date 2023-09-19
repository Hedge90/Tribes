package com.greenfoxacademy.springwebapp.dtos;

import java.util.List;

public class BattleResultDTO {

    private String victor;

    private List<Long> attackerCasualtyList;

    private List<Long> defenderCasualtyList;

    private List<Long> destroyedBuildingsList;

    private String kingdomDestroyed;

    public BattleResultDTO() {}

    public BattleResultDTO(String victor, List<Long> attackerCasualtyList, List<Long> defenderCasualtyList, List<Long> destroyedBuildingsList, String kingdomDestroyed) {
        this.victor = victor;
        this.attackerCasualtyList = attackerCasualtyList;
        this.defenderCasualtyList = defenderCasualtyList;
        this.destroyedBuildingsList = destroyedBuildingsList;
        this.kingdomDestroyed = kingdomDestroyed + " has been defeated as a result of this battle.";
    }

    public String getVictor() {
        return victor;
    }

    public void setVictor(String victor) {
        this.victor = victor;
    }

    public List<Long> getAttackerCasualtyList() {
        return attackerCasualtyList;
    }

    public void setAttackerCasualtyList(List<Long> attackerCasualtyList) {
        this.attackerCasualtyList = attackerCasualtyList;
    }

    public List<Long> getDefenderCasualtyList() {
        return defenderCasualtyList;
    }

    public void setDefenderCasualtyList(List<Long> defenderCasualtyList) {
        this.defenderCasualtyList = defenderCasualtyList;
    }

    public List<Long> getDestroyedBuildingsList() {
        return destroyedBuildingsList;
    }

    public void setDestroyedBuildingsList(List<Long> destroyedBuildingsList) {
        this.destroyedBuildingsList = destroyedBuildingsList;
    }

    public String getKingdomDestroyed() {
        return kingdomDestroyed;
    }

    public void setKingdomDestroyed(String kingdomDestroyed) {
        this.kingdomDestroyed = kingdomDestroyed;
    }
}
