package com.greenfoxacademy.springwebapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BuildingTypes type;

    private int level;

    private int hp;

    private Long startedAt;

    private Long finishedAt;

    private boolean underUpdate;

    private boolean destroyed;

    private int position;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Kingdom kingdom;

    public Building() {
    }

    public Building(Kingdom kingdom, BuildingTypes type, int hp) {
        this.type = type;
        this.kingdom = kingdom;
        this.kingdom.addBuilding(this);
        level = 1;
        this.hp = hp;
        startedAt = new Date(System.currentTimeMillis()).getTime();
        underUpdate = false;
    }

    public BuildingTypes getType() {
        return type;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setType(BuildingTypes type) {
        this.type = type;
    }

    public boolean getUnderUpdate() {
        return underUpdate;
    }

    public void setUnderUpdate(boolean underUpdate) {
        this.underUpdate = underUpdate;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
