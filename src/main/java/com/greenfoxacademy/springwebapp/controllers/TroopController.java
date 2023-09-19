package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.BuildingIDDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopListDTO;
import com.greenfoxacademy.springwebapp.services.TroopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/kingdom/troops")
public class TroopController {

    private TroopService troopService;

    @Autowired
    public TroopController(TroopService troopService) {
        this.troopService = troopService;
    }

    @PostMapping
    public ResponseEntity<TroopDTO> createTroop(@RequestBody BuildingIDDTO buildingIDDTO, HttpServletRequest request) {
        return new ResponseEntity<>(troopService.createNewTroopForUser(buildingIDDTO, request), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<TroopListDTO> getKingdomInformation(HttpServletRequest request) {
        TroopListDTO troopListDTO = new TroopListDTO(troopService.getTroopsInformation(request));
        return new ResponseEntity<>(troopListDTO, HttpStatus.OK);
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<TroopDTO> getTroop(@PathVariable Long id, HttpServletRequest request) {
        return new ResponseEntity<>(troopService.getTroopById(id, request), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> setTroopLevel(@PathVariable Long id, @RequestBody BuildingIDDTO buildingidDTO, HttpServletRequest request) {
        return new ResponseEntity<>(troopService.setTroopLevelbyId(id, buildingidDTO, request), HttpStatus.OK);

    }
}
