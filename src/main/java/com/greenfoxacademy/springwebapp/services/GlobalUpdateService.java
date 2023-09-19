package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Resource;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;

import java.util.List;

public interface GlobalUpdateService {
    void updateBuildings(List<Building> buildingList);

    List<Resource> updateResources(List<Resource> resourceList, List<Building> buildingList);

    int getHpForBuilding(BuildingTypes buildingType, int level);

    Long getTimeOfUpdateCompletion(Long startDate, Building building);
}
