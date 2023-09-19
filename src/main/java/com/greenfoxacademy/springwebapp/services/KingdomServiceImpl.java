package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.Resource;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.enums.ResourceType;
import com.greenfoxacademy.springwebapp.exception.InvalidIDException;
import com.greenfoxacademy.springwebapp.exception.KingdomNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class KingdomServiceImpl implements KingdomService {
    private TribesUserDetailsService tribesUserDetailsService;

    private MapperService mapperService;

    private KingdomRepository kingdomRepository;

    private GlobalUpdateService globalUpdateService;

    @Autowired
    public KingdomServiceImpl(TribesUserDetailsService tribesUserDetailsService, MapperService mapperService, KingdomRepository kingdomRepository, GlobalUpdateService globalUpdateService) {
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.mapperService = mapperService;
        this.kingdomRepository = kingdomRepository;
        this.globalUpdateService = globalUpdateService;
    }

    public Kingdom getKingdomByUser(HttpServletRequest request) {
        Kingdom kingdom = kingdomRepository.getKingdomByUser(tribesUserDetailsService.getUserByUsernameFromRequest(request)).orElseThrow(() -> new KingdomNotFoundException());
        globalUpdateService.updateResources(kingdom.getResources(), kingdom.getBuildings());
        return kingdom;
    }

    public int getTownHallLevel(Kingdom kingdom) {
        int townHallLevel = 0;
        for (Building building : kingdom.getBuildings()) {
            if (building.getType() == BuildingTypes.TOWNHALL && building.getFinishedAt() != null) {
                if (building.getLevel() > townHallLevel) {
                    townHallLevel = building.getLevel();
                }
            }
        }
        return townHallLevel;
    }

    public Building getTownHall(Kingdom kingdom) {
        for (Building building : kingdom.getBuildings()) {
            if (building.getType() == BuildingTypes.TOWNHALL) {
                return building;
            }
        }
        return null;
    }

    public KingdomDTO getKingdomInformation(HttpServletRequest request) {
        return mapperService.convertKingdomToKingdomDTO(getKingdomByUser(request));
    }

    public List<BuildingDTO> getBuildingsOfUser(HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        List<Building> buildingList = user.getKingdom().getBuildings();
        List<BuildingDTO> buildingDTOList = new ArrayList<>();
        for (Building building : buildingList) {
            buildingDTOList.add(mapperService.convertBuildingToBuildingDTO(building));
        }
        return buildingDTOList;
    }

    public ResourceListDTO getResourcesForAuthenticatedUser(HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        List<Resource> resourceList = globalUpdateService.updateResources(user.getKingdom().getResources(), user.getKingdom().getBuildings());
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        for (Resource resource : resourceList) {
            resourceDTOList.add(mapperService.convertResourceToResourceDTO(resource));
        }
        return new ResourceListDTO(resourceDTOList);
    }

    public KingdomDTO setKingdomName(HttpServletRequest request, @RequestBody KingdomNameDTO kingdomName) {
        Kingdom kingdom = getKingdomByUser(request);
        kingdom.setName(kingdomName.getName());
        return mapperService.convertKingdomToKingdomDTO(kingdom);
    }

    public KingdomDTO getKingdomById(Long id, HttpServletRequest request) {
        if (id < 1) {
            throw new InvalidIDException();
        }
        Kingdom kingdom = kingdomRepository.findById(id).orElseThrow(() -> new KingdomNotFoundException());
        return mapperService.convertKingdomToKingdomDTO(kingdom);
    }

    public void save(Kingdom kingdom) {
        kingdomRepository.save(kingdom);
    }

    public int getUserGold(User user) {
        for (Resource resource : user.getKingdom().getResources()) {
            if (resource.getType().equals(ResourceType.GOLD)) {
                return resource.getAmount();
            }
        }
        return 0;
    }
}
