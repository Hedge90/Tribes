package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingLevelDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingListDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingTypeDTO;
import com.greenfoxacademy.springwebapp.services.BuildingService;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/kingdom/buildings")
public class BuildingController {
    private KingdomService kingdomService;

    private BuildingService buildingService;

    @Autowired
    public BuildingController(KingdomService kingdomService, BuildingService buildingService) {
        this.kingdomService = kingdomService;
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<BuildingListDTO> getBuildings(HttpServletRequest request) {
        BuildingListDTO buildingListDTO = new BuildingListDTO(kingdomService.getBuildingsOfUser(request));
        if (buildingListDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(buildingListDTO, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getBuilding(@PathVariable Long id, HttpServletRequest request) {
        BuildingDTO buildingDTO = buildingService.getBuildingById(id, request);
        return new ResponseEntity<>(buildingDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BuildingDTO> createBuilding(@RequestBody BuildingTypeDTO buildingTypeDTO, HttpServletRequest request) {
        BuildingDTO buildingDTO = buildingService.createNewBuildingForUser(buildingTypeDTO, request);
        return new ResponseEntity<>(buildingDTO, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> upgradeBuilding(@PathVariable Long id, @RequestBody BuildingLevelDTO buildingLevelDTO, HttpServletRequest request) {
        return new ResponseEntity<>(buildingService.upgradeBuildingById(id, buildingLevelDTO.getLevel(), request), HttpStatus.OK);
    }
}