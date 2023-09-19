package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingTypeDTO;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.enums.ResourceType;
import com.greenfoxacademy.springwebapp.exception.*;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class BuildingServiceImpl implements BuildingService {
    private MapperService mapperService;

    private GlobalUpdateService globalUpdateService;

    private BuildingRepository buildingRepository;

    private TribesUserDetailsService tribesUserDetailsService;

    private KingdomService kingdomService;

    private GameLogicConfiguration configuration;

    @Autowired
    public BuildingServiceImpl(MapperService mapperService, GlobalUpdateService globalUpdateService, BuildingRepository buildingRepository, TribesUserDetailsService tribesUserDetailsService, KingdomService kingdomService, GameLogicConfiguration configuration) {
        this.mapperService = mapperService;
        this.globalUpdateService = globalUpdateService;
        this.buildingRepository = buildingRepository;
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.kingdomService = kingdomService;
        this.configuration = configuration;
    }

    public BuildingDTO getBuildingById(Long id, HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        Building building = getValidUserBuilding(user, id);
        return mapperService.convertBuildingToBuildingDTO(building);
    }

    public BuildingDTO createNewBuildingForUser(BuildingTypeDTO buildingTypeDTO, HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        if (buildingTypeDTO.getType() == null) {
            throw new MissingParameterException("type");
        }
        BuildingTypes type = null;
        if (buildingTypeDTO.getType().equals("farm")) {
            type = BuildingTypes.FARM;
        } else if (buildingTypeDTO.getType().equals("mine")) {
            type = BuildingTypes.MINE;
        } else if (buildingTypeDTO.getType().equals("academy")) {
            type = BuildingTypes.ACADEMY;
        }

        Kingdom kingdom = user.getKingdom();
        if (type == null) {
            throw new InvalidBuildingTypeException();
        } else if (kingdomService.getTownHallLevel(kingdom) == 0) {
            throw new InsufficientBuildingLevelException();
        }
        int cost = getBuildingCost(type, 1);
        if (kingdomService.getUserGold(user) < cost) {
            throw new NotEnoughResourcesException();
        }

        Building building = new Building(kingdom, type, globalUpdateService.getHpForBuilding(type, 1));
        building.setUnderUpdate(true);
        building.setPosition(buildingTypeDTO.getPosition());
        kingdom.decreaseResource(ResourceType.GOLD, cost);
        buildingRepository.save(building);
        return mapperService.convertBuildingToBuildingDTO(building);
    }

    public BuildingDTO upgradeBuildingById(Long id, Integer level, HttpServletRequest request) {
        if (level == null) {
            throw new MissingParameterException("level");
        }

        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        Building building = getValidUserBuilding(user, id);
        Kingdom kingdom = user.getKingdom();
        globalUpdateService.updateResources(user.getKingdom().getResources(), user.getKingdom().getBuildings());
        int cost = getBuildingCost(building.getType(), building.getLevel());

        updateBuildingsWhenPossible(building, kingdomService.getTownHall(kingdom), user, level, cost);
        startBuildingUpdate(building, new Date(System.currentTimeMillis()));

        kingdom.decreaseResource(ResourceType.GOLD, cost);
        buildingRepository.save(building);
        return mapperService.convertBuildingToBuildingDTO(building);
    }

    private void updateBuildingsWhenPossible(Building buildingToUpdate, Building townHallOfUser, User user,
                                             Integer targetBuildingLevel, int buildingCost) {
        if (buildingToUpdate.getLevel() + 1 != targetBuildingLevel) {
            throw new InvalidBuildingLevelException();
        } else if (townHallOfUser.getLevel() < targetBuildingLevel && townHallOfUser != buildingToUpdate) {
            throw new InsufficientBuildingLevelException();
        } else if (kingdomService.getUserGold(user) < buildingCost) {
            throw new NotEnoughResourcesException();
        } else if (buildingToUpdate.getUnderUpdate()) {
            if (buildingToUpdate.getFinishedAt() > new Date(System.currentTimeMillis()).getTime()) {
                throw new UnfinishedBuildingException();
            } else {
                globalUpdateService.updateBuildings(user.getKingdom().getBuildings());
            }
        }
    }

    private void startBuildingUpdate(Building building, Date startDate) {
        building.setUnderUpdate(true);
        building.setStartedAt(startDate.getTime());
        building.setFinishedAt(globalUpdateService.getTimeOfUpdateCompletion(startDate.getTime(), building));
    }

    private Building getValidUserBuilding(User user, Long buildingId) {
        Building building = buildingRepository.findById(buildingId).orElseThrow(() -> new BuildingNotFoundException());
        if (user.getId() != building.getKingdom().getUser().getId()) {
            throw new InvalidBuildingException();
        }
        return building;
    }

    private int getBuildingCost(BuildingTypes type, int level) {
        if (type.equals(BuildingTypes.TOWNHALL)) {
            return configuration.getTownhallCost() * level;
        } else if (type.equals(BuildingTypes.FARM)) {
            return configuration.getFarmCost() * level;
        } else if (type.equals(BuildingTypes.MINE)) {
            return configuration.getMineCost() * level;
        } else if (type.equals(BuildingTypes.ACADEMY)) {
            return configuration.getAcademyCost() * level;
        }
        return 0;
    }

    private Long getTimeOfUpdateCompletion(Long startDate, Building building) {
        Long finishedAt = null;
        if (building.getType().equals(BuildingTypes.TOWNHALL)) {
            finishedAt = startDate + (configuration.getTownhallTime() * 1000) * building.getLevel();
        } else if (building.getType().equals(BuildingTypes.FARM)) {
            finishedAt = startDate + (configuration.getFarmTime() * 1000) * building.getLevel();
        } else if (building.getType().equals(BuildingTypes.MINE)) {
            finishedAt = startDate + (configuration.getMineTime() * 1000) * building.getLevel();
        } else if (building.getType().equals(BuildingTypes.ACADEMY)) {
            finishedAt = startDate + (configuration.getAcademyTime() * 1000) * building.getLevel();
        }
        return finishedAt;
    }
}
