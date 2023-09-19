package com.greenfoxacademy.springwebapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.greenfoxacademy.springwebapp.enums.ResourceType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private int amount;

    private int generation;

    private Long updatedAt;

    @ManyToOne
    @JsonBackReference
    private Kingdom kingdom;

    public Resource() {
    }

    public Resource(ResourceType type, int amount, int generation, Kingdom kingdom) {
        this.type = type;
        this.amount = amount;
        this.generation = generation;
        this.updatedAt = new Date(System.currentTimeMillis()).getTime();
        this.kingdom = kingdom;
        kingdom.addResource(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getGeneration() {
        return generation;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }
}