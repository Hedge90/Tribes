package com.greenfoxacademy.springwebapp.dtos;

import com.greenfoxacademy.springwebapp.enums.BuildingTypes;


public class BuildingDTO {

    private Long id;

    private BuildingTypes type;

    private int level;

    private int hp;

    private Long startedAt;

    private Long finishedAt;

    private int position;

    public BuildingDTO() {
    }

    public BuildingDTO(Long id, BuildingTypes type, int level, int hp, Long startedAt, Long finishedAt, int position) {
        this.id = id;
        this.type = type;
        this.level = level;
        this.hp = hp;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BuildingTypes getType() {
        return type;
    }

    public void setType(BuildingTypes type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}