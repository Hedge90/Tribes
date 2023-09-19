package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingIDDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopDTO;
import com.greenfoxacademy.springwebapp.entities.Troop;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.exception.InvalidTroopException;
import com.greenfoxacademy.springwebapp.exception.NotEnoughResourcesException;
import com.greenfoxacademy.springwebapp.exception.NotValidAcademyException;
import com.greenfoxacademy.springwebapp.exception.TroopNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.TroopRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
class TroopServiceImplTest {

    TroopServiceImpl troopService;

    MapperService mapperService;

    TribesUserDetailsService tribesUserDetailsService;

    BuildingService buildingService;

    KingdomService kingdomService;

    GlobalUpdateService globalUpdateService;

    TroopRepository troopRepository;

    GameLogicConfiguration configuration;

    BeanFactory beanFactory;

    @Autowired
    TroopServiceImplTest(GameLogicConfiguration configuration, BeanFactory beanFactory) {
        mapperService = Mockito.mock(MapperService.class);
        tribesUserDetailsService = Mockito.mock(TribesUserDetailsService.class);
        buildingService = Mockito.mock(BuildingService.class);
        kingdomService = Mockito.mock(KingdomService.class);
        globalUpdateService = Mockito.mock(GlobalUpdateService.class);
        troopRepository = Mockito.mock(TroopRepository.class);
        this.configuration = configuration;
        troopService = new TroopServiceImpl(mapperService, tribesUserDetailsService, buildingService, kingdomService, globalUpdateService, troopRepository, configuration);
        this.beanFactory = beanFactory;
    }

    @Test
    public void createNewTroopForUser_WithCorrectDTOAndUserHavingAcademyWithMatchingId_ReturnsCorrectTroopDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        TroopDTO fakeTroopDTO = beanFactory.getBean("fakeTroopDTO", TroopDTO.class);
        BuildingDTO fakeAcademyBuildingDTO = beanFactory.getBean("fakeAcademyBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeAcademyBuildingDTO.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeAcademyBuildingDTO);
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(fakeUser.getKingdom().getResources().get(0).getAmount());
        Mockito.when(troopRepository.save((any(Troop.class)))).thenReturn(fakeUser.getKingdom().getTroops().get(0));
        Mockito.when(mapperService.convertTroopToTroopDTO((any(Troop.class)))).thenReturn(fakeTroopDTO);

        TroopDTO troopDTO = troopService.createNewTroopForUser(buildingIDDTO, request);

        Assertions.assertEquals(fakeTroopDTO.getLevel(), troopDTO.getLevel());
        Assertions.assertEquals(fakeTroopDTO.getHp(), troopDTO.getHp());
        Assertions.assertEquals(fakeTroopDTO.getAttack(), troopDTO.getAttack());
        Assertions.assertEquals(fakeTroopDTO.getDefence(), troopDTO.getDefence());
    }

    @Test
    void createNewTroopForUser_WithIdForNonAcademyBuilding_ThrowsNotValidAcademyException() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingDTO fakeFarmBuildingDTO = beanFactory.getBean("fakeFarmBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeUser.getKingdom().getBuildings().get(1).getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeFarmBuildingDTO);

        Assertions.assertThrows(NotValidAcademyException.class, () -> {
            troopService.createNewTroopForUser(buildingIDDTO, request);
        });
    }

    @Test
    void createNewTroopForUser_WithInsufficientGold_ThrowsNotEnoughResourcesException() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        fakeUser.getKingdom().getResources().get(0).setAmount(10);
        BuildingDTO fakeAcademyBuildingDTO = beanFactory.getBean("fakeAcademyBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeAcademyBuildingDTO.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeAcademyBuildingDTO);
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(0);

        Assertions.assertThrows(NotEnoughResourcesException.class, () -> {
            troopService.createNewTroopForUser(buildingIDDTO, request);
        });
    }

    @Test
    void getTroopsInformation_WithoutTroops_ReturnsEmptyTroopList() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);

        Assertions.assertEquals(new ArrayList<>(), troopService.getTroopsInformation(request));
    }

    @Test
    void getTroopsInformation_WithValidTroop_ReturnsCorrectTroopList() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        TroopDTO fakeTroopDTO = beanFactory.getBean("fakeTroopDTO", TroopDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(mapperService.convertTroopToTroopDTO(fakeUser.getKingdom().getTroops().get(0))).thenReturn(fakeTroopDTO);

        List<TroopDTO> troopDTOList = troopService.getTroopsInformation(request);

        Assertions.assertEquals(fakeTroopDTO.getId(), troopDTOList.get(0).getId());
        Assertions.assertEquals(fakeTroopDTO.getLevel(), troopDTOList.get(0).getLevel());
        Assertions.assertEquals(fakeTroopDTO.getHp(), troopDTOList.get(0).getHp());
        Assertions.assertEquals(fakeTroopDTO.getAttack(), troopDTOList.get(0).getAttack());
        Assertions.assertEquals(fakeTroopDTO.getDefence(), troopDTOList.get(0).getDefence());
        Assertions.assertEquals(fakeTroopDTO.getStartedAt(), troopDTOList.get(0).getStartedAt());
        Assertions.assertEquals(fakeTroopDTO.getFinishedAt(), troopDTOList.get(0).getFinishedAt());
    }

    @Test
    void getTroopById_WithTroopAndCorrectId_ReturnsCorrectTroopDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        TroopDTO fakeTroopDTO = beanFactory.getBean("fakeTroopDTO", TroopDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(troopRepository.findById(fakeTroopDTO.getId())).thenReturn(Optional.of(fakeUser.getKingdom().getTroops().get(0)));
        Mockito.when(mapperService.convertTroopToTroopDTO(fakeUser.getKingdom().getTroops().get(0))).thenReturn(fakeTroopDTO);

        TroopDTO storedTroopDTO = troopService.getTroopById(fakeTroopDTO.getId(), request);
        Assertions.assertEquals(fakeTroopDTO.getId(), storedTroopDTO.getLevel());
        Assertions.assertEquals(fakeTroopDTO.getHp(), storedTroopDTO.getHp());
        Assertions.assertEquals(fakeTroopDTO.getAttack(), storedTroopDTO.getAttack());
        Assertions.assertEquals(fakeTroopDTO.getDefence(), storedTroopDTO.getDefence());
        Assertions.assertEquals(fakeTroopDTO.getStartedAt(), storedTroopDTO.getStartedAt());
        Assertions.assertEquals(fakeTroopDTO.getFinishedAt(), storedTroopDTO.getFinishedAt());
    }

    @Test
    void getTroopById_WithInvalidTroopId_ThrowsTroopNotFoundException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(troopRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(TroopNotFoundException.class, () -> {
            troopService.getTroopById(1L, request);
        });
    }

    @Test
    void getTroopById_WithWrongUsersTroopId_ThrowsInvalidTroopException() {
        User fakeUser = beanFactory.getBean("fakeUserWithIds", User.class);
        User otherFakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        otherFakeUser.setUsername("otherFakeUser");
        otherFakeUser.setId(2L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(troopRepository.findById(otherFakeUser.getKingdom().getTroops().get(0).getId())).thenReturn(Optional.of(otherFakeUser.getKingdom().getTroops().get(0)));

        Assertions.assertThrows(InvalidTroopException.class, () -> {
            troopService.getTroopById(otherFakeUser.getKingdom().getTroops().get(0).getId(), request);
        });
    }

    @Test
    public void setTroopLevel_WithInvalidBuildingID_ThrowsNotValidAcademyException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeFarmBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeBuildingDTO.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeBuildingDTO);
        Mockito.when(troopRepository.findById(fakeUser.getKingdom().getTroops().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getTroops().get(0)));
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(fakeUser.getKingdom().getResources().get(0).getAmount());

        Assertions.assertThrows(NotValidAcademyException.class, () -> {
            troopService.setTroopLevelbyId(fakeUser.getKingdom().getTroops().get(0).getId(), buildingIDDTO, request);
        });
    }

    @Test
    public void setTroopLevel_WithValidInput_ReturnsCorrectTroopDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeAcademyBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeBuildingDTO.getId());
        TroopDTO fakeTroopDTO = beanFactory.getBean("fakeTroopDTO", TroopDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(troopRepository.findById(fakeUser.getKingdom().getTroops().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getTroops().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeBuildingDTO);
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(fakeUser.getKingdom().getResources().get(0).getAmount());
        Mockito.when(mapperService.convertTroopToTroopDTO((any(Troop.class)))).thenReturn(fakeTroopDTO);

        TroopDTO troopDTO = troopService.setTroopLevelbyId(fakeUser.getKingdom().getTroops().get(0).getId(), buildingIDDTO, request);

        Assertions.assertEquals(fakeTroopDTO.getLevel(), troopDTO.getLevel());
        Assertions.assertEquals(fakeTroopDTO.getHp(), troopDTO.getHp());
        Assertions.assertEquals(fakeTroopDTO.getAttack(), troopDTO.getAttack());
        Assertions.assertEquals(fakeTroopDTO.getDefence(), troopDTO.getDefence());
        Assertions.assertEquals(fakeTroopDTO.getFinishedAt(), troopDTO.getFinishedAt());
    }

    @Test
    public void setTroopLevel_WithInsufficientGold_ThrowsNotEnoughResourcesException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeAcademyBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(fakeBuildingDTO.getId());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(fakeBuildingDTO.getId(), request)).thenReturn(fakeBuildingDTO);
        Mockito.when(troopRepository.findById(fakeUser.getKingdom().getTroops().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getTroops().get(0)));
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(0);

        Assertions.assertThrows(NotEnoughResourcesException.class, () -> {
            troopService.setTroopLevelbyId(fakeUser.getKingdom().getTroops().get(0).getId(), buildingIDDTO, request);
        });
    }

    @Test
    public void setTroopLevel_WithVInvalidBuildingIDDTO_ThrowsTroopNotFoundException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeAcademyBuildingDTO", BuildingDTO.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(5L);
        TroopDTO fakeTroopDTO = beanFactory.getBean("fakeTroopDTO", TroopDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(troopRepository.findById(1L)).thenReturn(Optional.of(fakeUser.getKingdom().getTroops().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingService.getBuildingById(buildingIDDTO.getBuildingId(), request)).thenReturn(fakeBuildingDTO);
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(1000);

        Assertions.assertThrows(TroopNotFoundException.class, () -> {
            troopService.setTroopLevelbyId(2L, buildingIDDTO, request);
        });
    }
}