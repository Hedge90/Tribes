package com.greenfoxacademy.springwebapp.dtos;

import java.util.List;

public class KingdomDTO {

    private Long id;

    private String name;

    private Long userId;

    private List<BuildingDTO> buildings;

    private List<ResourceDTO> resources;

    private List<TroopDTO> troops;

    private LocationDTO location;

    public KingdomDTO() {
    }

    public KingdomDTO(Long id, String name, Long userId, List<BuildingDTO> buildings, List<ResourceDTO> resources, List<TroopDTO> troops, LocationDTO location) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.buildings = buildings;
        this.resources = resources;
        this.troops = troops;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<BuildingDTO> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingDTO> buildings) {
        this.buildings = buildings;
    }

    public List<ResourceDTO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceDTO> resources) {
        this.resources = resources;
    }

    public List<TroopDTO> getTroops() {
        return troops;
    }

    public void setTroops(List<TroopDTO> troops) {
        this.troops = troops;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}
