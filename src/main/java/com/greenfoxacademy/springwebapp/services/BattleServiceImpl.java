package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.BattleResultDTO;
import com.greenfoxacademy.springwebapp.dtos.BattleTroopListDTO;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.Troop;
import com.greenfoxacademy.springwebapp.exception.IllegalTroopSelectionException;
import com.greenfoxacademy.springwebapp.exception.InvalidIDException;
import com.greenfoxacademy.springwebapp.exception.KingdomAlreadyDefeatedException;
import com.greenfoxacademy.springwebapp.exception.KingdomNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BattleServiceImpl implements BattleService {

    private KingdomService kingdomService;

    private TribesUserDetailsService tribesUserDetailsService;

    private KingdomRepository kingdomRepository;

    private TroopRepository troopRepository;

    private BuildingRepository buildingRepository;

    private MapperService mapperService;

    private List<Troop> attackingTroopsList = new ArrayList<>();

    private List<Troop> defendingTroopsList = new ArrayList<>();

    private List<Building> defenderBuildingsList = new ArrayList<>();

    private List<Long> attackerCasualtyList = new ArrayList<>();

    private List<Long> defenderCasualtyList = new ArrayList<>();

    private List<Long> buildingsDestroyed = new ArrayList<>();

    Random random = new Random();

    @Autowired
    public BattleServiceImpl(KingdomService kingdomService, TribesUserDetailsService tribesUserDetailsService, KingdomRepository kingdomRepository, TroopRepository troopRepository, BuildingRepository buildingRepository, MapperService mapperService) {
        this.kingdomService = kingdomService;
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.kingdomRepository = kingdomRepository;
        this.troopRepository = troopRepository;
        this.buildingRepository = buildingRepository;
        this.mapperService = mapperService;
    }

    public BattleResultDTO resolveBattle(Long enemyKingdomId, BattleTroopListDTO battleTroopListDTO, HttpServletRequest httpServletRequest) {
        Kingdom attackingKingdom = tribesUserDetailsService.getUserByUsernameFromRequest(httpServletRequest).getKingdom();
        if (enemyKingdomId < 1) {
            throw new InvalidIDException();
        }
        Kingdom defendingKingdom = kingdomRepository.findById(enemyKingdomId).orElseThrow(KingdomNotFoundException::new);
        if (defendingKingdom.getDefeated()) {
            throw new KingdomAlreadyDefeatedException(defendingKingdom.getName());
        }

        setupAttackingTroopList(battleTroopListDTO, attackingKingdom);

        defendingTroopsList = defendingKingdom.getLivingTroops();

        defenderBuildingsList = defendingKingdom.getNonDestroyedBuildings();

        boolean battleOver = false;

        while (battleOver == false) {
            if (defendingTroopsList.isEmpty()) {
                break;
            }
            resolveCombatRound(attackingTroopsList, defendingTroopsList);
            attackingTroopsList = removeDeadTroopsFromBattle(attackingTroopsList);
            defendingTroopsList = removeDeadTroopsFromBattle(defendingTroopsList);
            if (attackingTroopsList.isEmpty() || defendingTroopsList.isEmpty() || kingdomService.getTownHall(defendingKingdom).isDestroyed()) {
                battleOver = true;
            }
        }

        String victor = determineResult(attackingKingdom, defendingKingdom);

        String destroyedKingdom = "No kingdom";
        if (defendingKingdom.getDefeated()) {
            destroyedKingdom = defendingKingdom.getName();
        }

        return new BattleResultDTO(victor, attackerCasualtyList, defenderCasualtyList, buildingsDestroyed, destroyedKingdom);
    }

    private void setupAttackingTroopList(BattleTroopListDTO battleTroopListDTO, Kingdom attackingKingdom) {
        for (Long id : battleTroopListDTO.getTroopIds()) {
            Optional<Troop> optionalTroop = troopRepository.findById(id);
            if (optionalTroop.isPresent()) {
                if (optionalTroop.get().getKingdom() != attackingKingdom || !optionalTroop.get().getAlive()) {
                    throw new IllegalTroopSelectionException();
                }
                attackingTroopsList.add(optionalTroop.get());
            }
        }
    }

    private void resolveCombatRound(List<Troop> attackingTroopsList, List<Troop> defendingTroopList) {
        Troop attackingTroop = selectCombatant(attackingTroopsList);
        Troop defendingTroop = selectCombatant(defendingTroopList);
        int damageDoneByAttacker = attackingTroop.getAttack() - defendingTroop.getDefence();
        int excessDamage = damageDoneByAttacker - defendingTroop.getHp();

        if (damageDoneByAttacker > 0) {
            applyDamageToTroop(defendingTroop, damageDoneByAttacker);
            if (!defenderCasualtyList.contains(defendingTroop.getId()) && !defendingTroop.getAlive()) {
                defenderCasualtyList.add(mapperService.convertTroopToTroopDTO(defendingTroop).getId());
            }
            if (defendingTroop.getHp() <= 0) {
                if (excessDamage > 0) {
                    applyDamageToBuilding(excessDamage);
                }
                defendingTroop = selectCombatant(defendingTroopList);
            }
        }
        int damageDoneByDefender = defendingTroop.getAttack() - attackingTroop.getDefence();
        if (damageDoneByDefender > 0) {
            applyDamageToTroop(attackingTroop, damageDoneByDefender);
            if (!attackerCasualtyList.contains(attackingTroop.getId()) && !attackingTroop.getAlive()) {
                attackerCasualtyList.add(mapperService.convertTroopToTroopDTO(attackingTroop).getId());
            }
        }
        troopRepository.save(attackingTroop);
        troopRepository.save(defendingTroop);
    }

    private Troop selectCombatant(List<Troop> troopList) {
        return troopList.get(random.nextInt(troopList.size()));
    }

    private void applyDamageToTroop(Troop damagedTroop, int damage) {
        damagedTroop.setHp(damagedTroop.getHp() - damage);
        if (damagedTroop.getHp() <= 0) {
            damagedTroop.setAlive(false);
        }
    }

    private List<Troop> removeDeadTroopsFromBattle(List<Troop> troopList) {
        List<Troop> remainingTroops = new ArrayList<>();
        for (Troop troop : troopList) {
            if (troop.getAlive()) {
                remainingTroops.add(troop);
            }
        }
        return remainingTroops;
    }

    private void applyDamageToBuilding(int damage) {
        Building building = defenderBuildingsList.get((random.nextInt(defenderBuildingsList.size())));
        building.setHp(building.getHp() - damage);
        if (building.getHp() <= 0) {
            building.setDestroyed(true);
            defenderBuildingsList.remove(building);
            buildingsDestroyed.add(building.getId());
            buildingRepository.save(building);
        }
    }

    private String determineResult(Kingdom attackingKingdom, Kingdom defendingKingdom) {
        String victor = null;
        if (attackingTroopsList.isEmpty()) {
            victor = defendingKingdom.getName();
        } else {
            victor = attackingKingdom.getName();
            defendingKingdom.setDefeated(true);
            kingdomRepository.save(defendingKingdom);
        }
        return victor;
    }
}
