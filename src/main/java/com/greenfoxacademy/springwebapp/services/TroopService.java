package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.BuildingIDDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopDTO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TroopService {

    TroopDTO createNewTroopForUser(BuildingIDDTO buildingIDDTO, HttpServletRequest request);

    List<TroopDTO> getTroopsInformation(HttpServletRequest request);

    TroopDTO getTroopById(Long id, HttpServletRequest request);

    @Transactional
    TroopDTO setTroopLevelbyId(Long id, BuildingIDDTO buildingidDTO, HttpServletRequest request);
}
