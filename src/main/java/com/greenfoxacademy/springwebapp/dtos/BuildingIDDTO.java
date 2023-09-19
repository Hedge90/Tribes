package com.greenfoxacademy.springwebapp.dtos;

public class BuildingIDDTO {

    private Long buildingId;

    public BuildingIDDTO() {}

    public BuildingIDDTO(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }
}
