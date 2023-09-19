package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.KingdomDTO;
import com.greenfoxacademy.springwebapp.dtos.KingdomNameDTO;
import com.greenfoxacademy.springwebapp.services.GeneralUtility;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/kingdom")
public class KingdomController {
    private KingdomService kingdomService;

    @Autowired
    public KingdomController(KingdomService kingdomService) {
        this.kingdomService = kingdomService;
    }

    @GetMapping
    public ResponseEntity<KingdomDTO> getKingdomInformation(HttpServletRequest request) {
        return new ResponseEntity<>(kingdomService.getKingdomInformation(request), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> setKingdomName(HttpServletRequest request, @RequestBody KingdomNameDTO kingdomName) {
        if (GeneralUtility.isEmptyOrNull(kingdomName.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(kingdomService.setKingdomName(request, kingdomName), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getKingdomById(@PathVariable Long id, HttpServletRequest request) {
        return new ResponseEntity<>(kingdomService.getKingdomById(id, request), HttpStatus.OK);
    }
}
