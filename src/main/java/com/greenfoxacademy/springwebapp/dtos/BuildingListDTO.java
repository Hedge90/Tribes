package com.greenfoxacademy.springwebapp.dtos;

import java.util.ArrayList;
import java.util.List;

public class BuildingListDTO {

    private List<BuildingDTO> buildings = new ArrayList<>();

    public BuildingListDTO() {}

    public BuildingListDTO(List<BuildingDTO> buildings) {
        this.buildings = buildings;
    }

    public List<BuildingDTO> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingDTO> buildings) {
        this.buildings = buildings;
    }
}
