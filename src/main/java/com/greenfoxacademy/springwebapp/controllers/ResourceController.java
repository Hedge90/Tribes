package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.ResourceListDTO;
import com.greenfoxacademy.springwebapp.services.KingdomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/kingdom/resources")
public class ResourceController {
    private KingdomService kingdomService;

    @Autowired
    public ResourceController(KingdomService kingdomService) {
        this.kingdomService = kingdomService;
    }

    @GetMapping
    public ResponseEntity<ResourceListDTO> getResources(HttpServletRequest request) {
        return new ResponseEntity<>(kingdomService.getResourcesForAuthenticatedUser(request), HttpStatus.OK);
    }
}