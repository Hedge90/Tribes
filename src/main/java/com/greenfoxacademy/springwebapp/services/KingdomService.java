package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.KingdomDTO;
import com.greenfoxacademy.springwebapp.dtos.KingdomNameDTO;
import com.greenfoxacademy.springwebapp.dtos.ResourceListDTO;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.User;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface KingdomService {

    Kingdom getKingdomByUser(HttpServletRequest request);

    int getTownHallLevel(Kingdom kingdom);

    Building getTownHall(Kingdom kingdom);

    KingdomDTO getKingdomInformation(HttpServletRequest request);

    public List<BuildingDTO> getBuildingsOfUser(HttpServletRequest request);

    ResourceListDTO getResourcesForAuthenticatedUser(HttpServletRequest request);

    @Transactional
    KingdomDTO setKingdomName(HttpServletRequest request, KingdomNameDTO kingdomNameDTO);

    KingdomDTO getKingdomById(Long id, HttpServletRequest request);

    void save(Kingdom kingdom);

    public int getUserGold(User user);
}
