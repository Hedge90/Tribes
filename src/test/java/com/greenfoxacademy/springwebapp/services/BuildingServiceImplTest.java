package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingTypeDTO;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.exception.*;
import com.greenfoxacademy.springwebapp.repositories.BuildingRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
class BuildingServiceImplTest {

    BuildingServiceImpl buildingService;

    MapperService mapperService;

    GlobalUpdateService globalUpdateService;

    BuildingRepository buildingRepository;

    TribesUserDetailsService tribesUserDetailsService;

    KingdomService kingdomService;

    GameLogicConfiguration configuration;

    BeanFactory beanFactory;

    @Autowired
    BuildingServiceImplTest(GameLogicConfiguration configuration, BeanFactory beanFactory) {
        mapperService = Mockito.mock(MapperService.class);
        globalUpdateService = Mockito.mock(GlobalUpdateService.class);
        buildingRepository = Mockito.mock(BuildingRepository.class);
        tribesUserDetailsService = Mockito.mock(TribesUserDetailsService.class);
        kingdomService = Mockito.mock(KingdomService.class);
        this.configuration = configuration;
        buildingService = new BuildingServiceImpl(mapperService, globalUpdateService,buildingRepository, tribesUserDetailsService, kingdomService, configuration);
        this.beanFactory = beanFactory;
    }

    @Test
    void getBuildingById_WithUserMatchingId_ReturnsCorrectBuildingDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeTownHallBuildingDTO", BuildingDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingRepository.findById(1L)).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(0)));
        Mockito.when(mapperService.convertBuildingToBuildingDTO(fakeUser.getKingdom().getBuildings().get(0))).thenReturn(fakeBuildingDTO);
        BuildingDTO storedBuilding = buildingService.getBuildingById(1L, request);

        Assertions.assertEquals(fakeBuildingDTO.getType(), storedBuilding.getType());
        Assertions.assertEquals(fakeBuildingDTO.getHp(), storedBuilding.getHp());
        Assertions.assertEquals(fakeBuildingDTO.getLevel(), storedBuilding.getLevel());
        Assertions.assertEquals(fakeBuildingDTO.getStartedAt(), storedBuilding.getStartedAt());
    }

    @Test
    void getBuildingById_WithNoMatchingBuildingId_ThrowsBuildingNotFound() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(buildingRepository.findById(2L)).thenThrow(new BuildingNotFoundException());

        Assertions.assertThrows(BuildingNotFoundException.class, () -> {
            buildingService.getBuildingById(2L, request);
        });
    }

    @Test
    void getBuildingById_WithBuildingNotValidForUser_ThrowsInvalidBuildingException() {
        User fakeUser = beanFactory.getBean("fakeUserWithIds", User.class);
        User otherFakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        otherFakeUser.setUsername("otherFakeUser");
        otherFakeUser.setId(2L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(buildingRepository.findById(otherFakeUser.getKingdom().getBuildings().get(0).getId())).thenReturn(Optional.of(otherFakeUser.getKingdom().getBuildings().get(0)));

        Assertions.assertThrows(InvalidBuildingException.class, () -> {
            buildingService.getBuildingById(otherFakeUser.getKingdom().getBuildings().get(0).getId(), request);
        });
    }

    @Test
    void createNewBuildingForUser_WithProperTypeAndEnoughResources_ReturnsValidBuildingDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeMineBuildingDTO", BuildingDTO.class);
        BuildingTypeDTO buildingTypeDTO = new BuildingTypeDTO("mine", 1);
        int beforeCreateNewBuildingForUserGold = fakeUser.getKingdom().getResources().get(0).getAmount();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHallLevel(fakeUser.getKingdom())).thenReturn(1);
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(beforeCreateNewBuildingForUserGold);
        Mockito.when(mapperService.convertBuildingToBuildingDTO(any(Building.class))).thenReturn(fakeBuildingDTO);

        BuildingDTO returnedDTO = buildingService.createNewBuildingForUser(buildingTypeDTO, request);

        Assertions.assertEquals(fakeBuildingDTO.getType(), returnedDTO.getType());
        Assertions.assertEquals(fakeBuildingDTO.getLevel(), returnedDTO.getLevel());
        Assertions.assertEquals(fakeBuildingDTO.getHp(), returnedDTO.getHp());
        Assertions.assertEquals(beforeCreateNewBuildingForUserGold - configuration.getMineCost(), fakeUser.getKingdom().getResources().get(0).getAmount());
    }

    @Test
    void createNewBuildingForUser_WithMissingTypeParameter_ThrowsMissingParameterException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        BuildingTypeDTO buildingTypeDTO = new BuildingTypeDTO();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);

        Assertions.assertThrows(MissingParameterException.class, () -> {
            buildingService.createNewBuildingForUser(buildingTypeDTO, request);
        });
    }

    @Test
    void createNewBuildingForUser_WithUserNotHavingTownHall_ThrowsInsufficientBuildingLevelException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        BuildingTypeDTO buildingTypeDTO = new BuildingTypeDTO("farm", 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHallLevel(fakeUser.getKingdom())).thenReturn(0);

        Assertions.assertThrows(InsufficientBuildingLevelException.class, () -> {
            buildingService.createNewBuildingForUser(buildingTypeDTO, request);
        });
    }

    @Test
    void createNewBuildingForUser_WithInvalidBuildingType_ThrowsInvalidBuildingTypeException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        BuildingTypeDTO buildingTypeDTO = new BuildingTypeDTO("form", 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHallLevel(fakeUser.getKingdom())).thenReturn(1);

        Assertions.assertThrows(InvalidBuildingTypeException.class, () -> {
            buildingService.createNewBuildingForUser(buildingTypeDTO, request);
        });
    }

    @Test
    void createNewBuildingForUser_WithInsufficientResources_ThrowsNotEnoughResourcesException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        BuildingTypeDTO buildingTypeDTO = new BuildingTypeDTO("farm", 1);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHallLevel(fakeUser.getKingdom())).thenReturn(1);

        Assertions.assertThrows(NotEnoughResourcesException.class, () -> {
            buildingService.createNewBuildingForUser(buildingTypeDTO, request);
        });
    }

    @Test
    void upgradeBuildingById_WithValidBuildingTargetLevel_ReturnsValidBuildingDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        BuildingDTO fakeBuildingDTO = beanFactory.getBean("fakeTownHallBuildingDTO", BuildingDTO.class);
        int beforeCreateNewBuildingForUserGold = fakeUser.getKingdom().getResources().get(0).getAmount();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(0));
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(beforeCreateNewBuildingForUserGold);
        Mockito.when(mapperService.convertBuildingToBuildingDTO(any(Building.class))).thenReturn(fakeBuildingDTO);

        BuildingDTO returnedDTO = buildingService.upgradeBuildingById(fakeUser.getKingdom().getBuildings().get(0).getId(), 2, request);

        Assertions.assertEquals(fakeBuildingDTO.getType(), returnedDTO.getType());
        Assertions.assertEquals(fakeBuildingDTO.getLevel(), returnedDTO.getLevel());
        Assertions.assertEquals(fakeBuildingDTO.getHp(), returnedDTO.getHp());
        Assertions.assertEquals(beforeCreateNewBuildingForUserGold - configuration.getTownhallCost(), fakeUser.getKingdom().getResources().get(0).getAmount());
    }

    @Test
    void upgradeBuildingById_WithInsufficientResources_ThrowsNotEnoughResourcesException() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        fakeUser.getKingdom().getResources().get(0).setAmount(0);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(0));

        Assertions.assertThrows(NotEnoughResourcesException.class, () -> {
            buildingService.upgradeBuildingById(fakeUser.getKingdom().getBuildings().get(0).getId(), 2, request);
        });
    }

    @Test
    void upgradeBuildingById_WithInvalidId_ThrowsBuildingNotFoundException() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        User otherFakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        otherFakeUser.setUsername("otherFakeUser");
        otherFakeUser.getKingdom().getBuildings().get(0).setId(10L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(0).getId())).thenReturn(Optional.of(otherFakeUser.getKingdom().getBuildings().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(0));

        Assertions.assertThrows(BuildingNotFoundException.class, () -> {
            buildingService.upgradeBuildingById(otherFakeUser.getKingdom().getBuildings().get(0).getId(), 2, request);
        });
    }

    @Test
    void upgradeBuildingById_WithInvalidBuildingLevel_ThrowsInvalidBuildingLevelException() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(0).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(0)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(0));

        Assertions.assertThrows(InvalidBuildingLevelException.class, () -> {
            buildingService.upgradeBuildingById(fakeUser.getKingdom().getBuildings().get(0).getId(), 99, request);
        });
    }

    @Test
    void upgradeBuildingById_WithSameTownHallLevel_ThrowsInsufficientBuildingLevelException() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(1).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(1)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(0));
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(fakeUser.getKingdom().getResources().get(0).getAmount());

        Assertions.assertThrows(InsufficientBuildingLevelException.class, () -> {
            buildingService.upgradeBuildingById(fakeUser.getKingdom().getBuildings().get(1).getId(), 2, request);
        });
    }

    @Test
    void upgradeBuildingById_WithBuildingUnderUpgrade_ThrowsUnfinishedBuildingException() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        fakeUser.getKingdom().getBuildings().get(2).setUnderUpdate(true);
        fakeUser.getKingdom().getBuildings().get(2).setFinishedAt(fakeUser.getKingdom().getBuildings().get(2).getFinishedAt() + 9999999);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(buildingRepository.findById(fakeUser.getKingdom().getBuildings().get(2).getId())).thenReturn(Optional.of(fakeUser.getKingdom().getBuildings().get(2)));
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomService.getTownHall(fakeUser.getKingdom())).thenReturn(fakeUser.getKingdom().getBuildings().get(2));
        Mockito.when(kingdomService.getUserGold(fakeUser)).thenReturn(fakeUser.getKingdom().getResources().get(0).getAmount());

        Assertions.assertThrows(UnfinishedBuildingException.class, () -> {
            buildingService.upgradeBuildingById(fakeUser.getKingdom().getBuildings().get(2).getId(), 2, request);
        });
    }

    @Test
    public void upgradeBuildingById_WithMissingLevelParameter_ThrowsMissingParameterException() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Assertions.assertThrows(MissingParameterException.class, () -> {
            buildingService.upgradeBuildingById(1L, null, request);
        });
    }
}