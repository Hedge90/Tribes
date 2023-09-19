package com.greenfoxacademy.springwebapp.dtos;

import java.util.List;

public class BattleTroopListDTO {

    private List<Long> troopIds;

    public BattleTroopListDTO() {}

    public BattleTroopListDTO(List<Long> troopIds) {
        this.troopIds = troopIds;
    }

    public List<Long> getTroopIds() {
        return troopIds;
    }

    public void setTroopIds(List<Long> troopIds) {
        this.troopIds = troopIds;
    }
}
