package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingIDDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopDTO;
import com.greenfoxacademy.springwebapp.entities.Troop;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.enums.BuildingTypes;
import com.greenfoxacademy.springwebapp.enums.ResourceType;
import com.greenfoxacademy.springwebapp.exception.InvalidTroopException;
import com.greenfoxacademy.springwebapp.exception.NotEnoughResourcesException;
import com.greenfoxacademy.springwebapp.exception.NotValidAcademyException;
import com.greenfoxacademy.springwebapp.exception.TroopNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class TroopServiceImpl implements TroopService {

    private MapperService mapperService;

    private GlobalUpdateService globalUpdateService;

    private TribesUserDetailsService tribesUserDetailsService;

    private BuildingService buildingService;

    private KingdomService kingdomService;

    private TroopRepository troopRepository;

    private GameLogicConfiguration configuration;

    @Autowired
    public TroopServiceImpl(MapperService mapperService, TribesUserDetailsService tribesUserDetailsService, BuildingService buildingService, KingdomService kingdomService, GlobalUpdateService globalUpdateService, TroopRepository troopRepository, GameLogicConfiguration configuration) {
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.buildingService = buildingService;
        this.kingdomService = kingdomService;
        this.globalUpdateService = globalUpdateService;
        this.troopRepository = troopRepository;
        this.mapperService = mapperService;
        this.configuration = configuration;
    }

    public TroopDTO createNewTroopForUser(BuildingIDDTO buildingidDTO, HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        globalUpdateService.updateBuildings(user.getKingdom().getBuildings());
        BuildingDTO sourceBuilding = buildingService.getBuildingById(buildingidDTO.getBuildingId(), request);
        ensureValidAcademyBuilding(buildingidDTO, sourceBuilding);
        int cost = configuration.getTroopCost() * sourceBuilding.getLevel();
        validateUserHasEnoughGold(user, cost);

        user.getKingdom().decreaseResource(ResourceType.GOLD, cost);
        Troop troop = troopRepository.save(new Troop(user.getKingdom(), sourceBuilding.getLevel()));
        return mapperService.convertTroopToTroopDTO(troop);
    }


    public List<TroopDTO> getTroopsInformation(HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        List<TroopDTO> troopDTOList = new ArrayList<>();
        for (Troop troop : user.getKingdom().getTroops()) {
            troopDTOList.add(mapperService.convertTroopToTroopDTO(troop));
        }
        return troopDTOList;
    }

    public TroopDTO getTroopById(Long id, HttpServletRequest request) {
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        Troop troop = getValidUserTroop(user, id);
        return mapperService.convertTroopToTroopDTO(troop);
    }

    private Troop getValidUserTroop(User user, Long troopId) {
        Troop troop = troopRepository.findById(troopId).orElseThrow(() -> new TroopNotFoundException());
        if (user.getId() != troop.getKingdom().getUser().getId()) {
            throw new InvalidTroopException();
        }
        return troop;
    }

    public TroopDTO setTroopLevelbyId(Long id, BuildingIDDTO buildingidDTO, HttpServletRequest request) {
        Troop troop = troopRepository.findById(id).orElseThrow(TroopNotFoundException::new);
        BuildingDTO sourceBuilding = buildingService.getBuildingById(buildingidDTO.getBuildingId(), request);
        User user = tribesUserDetailsService.getUserByUsernameFromRequest(request);
        ensureValidAcademyBuilding(buildingidDTO, sourceBuilding);
        globalUpdateService.updateResources(user.getKingdom().getResources(), user.getKingdom().getBuildings());
        int troopCost = configuration.getTroopCost() * sourceBuilding.getLevel();
        validateUserHasEnoughGold(user, troopCost);
        troop.setStartedAt(System.currentTimeMillis());
        troop.setFinishedAt(System.currentTimeMillis() + ((long) sourceBuilding.getLevel() * configuration.getTroopTime()));
        troopRepository.save(troop);
        user.getKingdom().decreaseResource(ResourceType.GOLD, troopCost);
        return mapperService.convertTroopToTroopDTO(troop);
    }

    private static void ensureValidAcademyBuilding(BuildingIDDTO buildingidDTO, BuildingDTO sourceBuilding) {
        if (buildingidDTO.getBuildingId() == null || sourceBuilding.getType() != BuildingTypes.ACADEMY || sourceBuilding.getFinishedAt() == null) {
            throw new NotValidAcademyException();
        }
    }

    private void validateUserHasEnoughGold(User user, int troopCost) {
        if (troopCost > kingdomService.getUserGold(user)) {
            throw new NotEnoughResourcesException();
        }
    }
}
