package com.greenfoxacademy.springwebapp.dtos;

public class BuildingTypeDTO {

    private String type;

    private int position;

    public BuildingTypeDTO() {}

    public BuildingTypeDTO(String type, int position) {
        this.type = type;
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
