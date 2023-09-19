package com.greenfoxacademy.springwebapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.greenfoxacademy.springwebapp.enums.ResourceType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kingdoms")
public class Kingdom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kingdom_name")
    private String name;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private User user;

    @OneToOne(cascade = CascadeType.PERSIST, mappedBy = "kingdom")
    @JsonManagedReference
    private Location location;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "kingdom", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Building> buildings;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, mappedBy = "kingdom")
    @JsonManagedReference
    private List<Resource> resources;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH}, mappedBy = "kingdom")
    @JsonManagedReference
    private List<Troop> troops;

    private boolean defeated;

    public Kingdom() {
    }

    public Kingdom(String name, User user) {
        this.name = name;
        this.user = user;
        user.setKingdom(this);
        location = new Location();
        location.setKingdom(this);
        buildings = new ArrayList<>();
        resources = new ArrayList<>();
        troops = new ArrayList<>();
        this.defeated = false;
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

    public User getUser() {
        return user;
    }

    public Location getLocation() {
        return location;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Building> getNonDestroyedBuildings() {
        List<Building> nonDestroyedBuildings = new ArrayList<>();
        for (Building building : buildings) {
            if (!building.isDestroyed()) {
                nonDestroyedBuildings.add(building);
            }
        }
        return nonDestroyedBuildings;
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void decreaseResource(ResourceType type, int amount) {
        for (Resource resource : resources) {
            if (resource.getType() == type) {
                resource.setAmount(resource.getAmount() - amount);
            }
        }
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public List<Troop> getLivingTroops() {
        List<Troop> aliveTroops = new ArrayList<>();
        for (Troop troop : troops) {
            if (troop.getAlive()) {
                aliveTroops.add(troop);
            }
        }
        return aliveTroops;
    }

    public void addTroop(Troop troop) {
        troops.add(troop);
    }

    public boolean getDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }
}
