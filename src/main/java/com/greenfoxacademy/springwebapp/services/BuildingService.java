package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingTypeDTO;

import javax.servlet.http.HttpServletRequest;

public interface BuildingService {
    public BuildingDTO getBuildingById(Long id, HttpServletRequest request);

    public BuildingDTO createNewBuildingForUser(BuildingTypeDTO buildingTypeDTO, HttpServletRequest request);

    public BuildingDTO upgradeBuildingById(Long id, Integer level, HttpServletRequest request);
}
