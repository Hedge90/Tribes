package com.greenfoxacademy.springwebapp.dtos;

import java.util.ArrayList;
import java.util.List;

public class TroopListDTO {
    private List<TroopDTO> troops;

    public TroopListDTO() {
        troops = new ArrayList<>();
    }

    public TroopListDTO(List<TroopDTO> troops) {
        this.troops = troops;
    }

    public List<TroopDTO> getTroops() {
        return troops;
    }
}
