package com.greenfoxacademy.springwebapp.dtos;

public class BuildingLevelDTO {
    private Integer level;

    public BuildingLevelDTO() {
    }

    public BuildingLevelDTO(Integer level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
