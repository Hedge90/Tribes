package com.greenfoxacademy.springwebapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.greenfoxacademy.springwebapp.services.GeneralUtility;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer x;

    private Integer y;

    @OneToOne
    @JsonBackReference
    private Kingdom kingdom;

    public Location() {
        x = GeneralUtility.generateRandomNumber(100);
        y = GeneralUtility.generateRandomNumber(100);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }
}
