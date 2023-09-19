package com.greenfoxacademy.springwebapp.dtos;

import com.greenfoxacademy.springwebapp.enums.ResourceType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class ResourceDTO {
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private int amount;

    private int generation;

    private Long updatedAt;

    public ResourceDTO() {
    }

    public ResourceDTO(ResourceType type, int amount, int generation, Long updatedAt) {
        this.type = type;
        this.amount = amount;
        this.generation = generation;
        this.updatedAt = updatedAt;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
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

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}