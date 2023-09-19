package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Resource;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.exception.InvalidBuildingTypeException;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalUpdateServiceImpl implements GlobalUpdateService {
    private GameLogicConfiguration configuration;

    private KingdomRepository kingdomRepository;

    private BuildingRepository buildingRepository;

    private ResourceRepository resourceRepository;

    private TimeService timeService;

    public GlobalUpdateServiceImpl(GameLogicConfiguration configuration, KingdomRepository kingdomRepository, ResourceRepository resourceRepository, BuildingRepository buildingRepository, TimeService timeService) {
        this.configuration = configuration;
        this.kingdomRepository = kingdomRepository;
        this.resourceRepository = resourceRepository;
        this.buildingRepository = buildingRepository;
        this.timeService = timeService;
    }

    public void updateBuildings(List<Building> buildingList) {
        boolean update = false;
        for (Building building : buildingList) {
            if (building.getFinishedAt() == null) {
                if (System.currentTimeMillis() > (building.getStartedAt() + (configuration.getMineTime() * 1000)) && building.getUnderUpdate()) {
                    building.setFinishedAt(getTimeOfUpdateCompletion(building.getStartedAt(), building));
                    building.setUnderUpdate(false);
                    update = true;
                }
            } else {
                if (System.currentTimeMillis() > building.getFinishedAt() && building.getUnderUpdate()) {
                    building.setLevel(building.getLevel() + 1);
                    building.setHp(getHpForBuilding(building.getType(), building.getLevel()));
                    building.setFinishedAt(getTimeOfUpdateCompletion(building.getStartedAt(), building));
                    building.setUnderUpdate(false);
                    update = true;
                }
            }
        }
        if (update) {
            kingdomRepository.save(buildingList.get(0).getKingdom());
        }
    }

    public int getHpForBuilding(BuildingTypes buildingType, int level) {
        switch (buildingType) {
            case TOWNHALL:
                return configuration.getTownhallHp() * level;
            case FARM:
                return configuration.getFarmHp() * level;
            case MINE:
                return configuration.getMineHp() * level;
            case ACADEMY:
                return configuration.getAcademyHp() * level;
            default:
                throw new InvalidBuildingTypeException();
        }
    }

    public Long getTimeOfUpdateCompletion(Long startDate, Building building) {
        Long finishedAt = null;
        switch (building.getType()) {
            case TOWNHALL:
                finishedAt = startDate + (configuration.getTownhallTime() * 1000) * building.getLevel();
                break;
            case FARM:
                finishedAt = startDate + (configuration.getFarmTime() * 1000) * building.getLevel();
                break;
            case MINE:
                finishedAt = startDate + (configuration.getMineTime() * 1000) * building.getLevel();
                break;
            case ACADEMY:
                finishedAt = startDate + (configuration.getAcademyTime() * 1000) * building.getLevel();
                break;
            default:
                throw new InvalidBuildingTypeException();
        }
        return finishedAt;
    }

    public List<Resource> updateResources(List<Resource> resourceList, List<Building> buildingList) {
        updateBuildings(buildingList);
        for (Building building : buildingList) {
            if (building.getFinishedAt() != null) {
                if (building.getType() == BuildingTypes.FARM) {
                    resourceList.set(1, updateResourceForBuilding(resourceList.get(1), building, configuration.getGoldResourceIncreaser()));
                }

                if (building.getType() == BuildingTypes.MINE) {
                    resourceList.set(0, updateResourceForBuilding(resourceList.get(0), building, configuration.getFoodResourceIncreaser()));
                }
            }
        }
        resourceList.get(1).setUpdatedAt(System.currentTimeMillis());
        resourceList.get(0).setUpdatedAt(System.currentTimeMillis());

        resourceRepository.save(resourceList.get(1));
        resourceRepository.save(resourceList.get(0));
        return resourceList;
    }

    public Resource updateResourceForBuilding(Resource resource, Building building, int resourceIncreaser) {
        double timeDifferenceInMinutes = timeService.getTimeDifferenceInMinutesBetweenGivenTimeAndCurrentTime(resource.getUpdatedAt());
        if (resource.getUpdatedAt() < building.getFinishedAt() && building.getLevel() > 1) {
            double timeDifferenceInMinutesBeforeLevelUpdated = timeService.getTimeDifferenceInMinutesBetweenTwoGivenTime(building.getFinishedAt(), resource.getUpdatedAt());
            resource.setAmount(resource.getAmount() + ((int) ((((building.getLevel() - 1) * resourceIncreaser) + resourceIncreaser) * timeDifferenceInMinutesBeforeLevelUpdated)));
        }
        resource.setAmount(resource.getAmount() + ((int) (((building.getLevel() * resourceIncreaser) + resourceIncreaser) * timeDifferenceInMinutes)));
        return resource;
    }
}
