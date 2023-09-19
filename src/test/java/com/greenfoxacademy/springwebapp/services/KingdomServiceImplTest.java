package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.Kingdom;
import com.greenfoxacademy.springwebapp.entities.Resource;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.exception.KingdomNotFoundException;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import com.greenfoxacademy.springwebapp.repositories.KingdomRepository;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class KingdomServiceImplTest {
    KingdomServiceImpl kingdomService;

    MapperService mapperService;

    TribesUserDetailsService tribesUserDetailsService;

    private static GlobalUpdateService globalUpdateService;

    KingdomRepository kingdomRepository;

    BeanFactory beanFactory;

    @Autowired
    KingdomServiceImplTest(BeanFactory beanFactory) {
        tribesUserDetailsService = Mockito.mock(TribesUserDetailsService.class);
        mapperService = Mockito.mock(MapperService.class);
        kingdomRepository = Mockito.mock(KingdomRepository.class);
        globalUpdateService = Mockito.mock(GlobalUpdateService.class);
        kingdomService = new KingdomServiceImpl(tribesUserDetailsService, mapperService, kingdomRepository, globalUpdateService);
        this.beanFactory = beanFactory;
    }

    @Test
    void getKingdomInformation_WithValidKingdomDTO_ReturnValidKingdomDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        KingdomDTO fakeKingdomDTO = beanFactory.getBean("fakeKingdomDTO", KingdomDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomRepository.getKingdomByUser(fakeUser)).thenReturn(Optional.of(fakeUser.getKingdom()));
        Mockito.when(mapperService.convertKingdomToKingdomDTO(fakeUser.getKingdom())).thenReturn(fakeKingdomDTO);

        KingdomDTO storedKingdom = kingdomService.getKingdomInformation(request);
        assertEquals(fakeKingdomDTO.getName(), storedKingdom.getName());
    }

    @Test
    void getKingdomInformation_WithInvalidKingdomDTO_ReturnNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Assertions.assertThrows(KingdomNotFoundException.class, () -> kingdomService.getKingdomInformation(request));
    }

    @Test
    void setKingdomName_WithValidKingdomName_ReturnValidKingdomDTOWithTheNewKingdomName() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        KingdomNameDTO kingdomNameDTO = new KingdomNameDTO("New Fake Kingdom");
        KingdomDTO fakeKingdomDTO = beanFactory.getBean("fakeKingdomDTO", KingdomDTO.class);
        fakeKingdomDTO.setName(kingdomNameDTO.getName());
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomRepository.getKingdomByUser(fakeUser)).thenReturn(Optional.of(fakeUser.getKingdom()));
        Mockito.when(mapperService.convertKingdomToKingdomDTO(fakeUser.getKingdom())).thenReturn(fakeKingdomDTO);

        KingdomDTO storedKingdom = kingdomService.setKingdomName(request, kingdomNameDTO);
        Assertions.assertEquals(kingdomNameDTO.getName(), storedKingdom.getName());
    }

    @Test
    void setKingdomName_WithNotExistingUserByUsername_ThrowsException() {
        KingdomNameDTO kingdomNameDTO = new KingdomNameDTO("New Fake Kingdom");
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenThrow(new UserNotFoundException("Someone"));

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            kingdomService.setKingdomName(request, kingdomNameDTO);
        });
    }

    @Test
    void getBuildingsOfUser_WithValidInputId_ReturnsValidList() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingDTO fakeTownHallBuildingDTO = beanFactory.getBean("fakeTownHallBuildingDTO", BuildingDTO.class);
        BuildingDTO fakeFarmBuildingDTO = beanFactory.getBean("fakeFarmBuildingDTO", BuildingDTO.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(mapperService.convertBuildingToBuildingDTO(fakeUser.getKingdom().getBuildings().get(0))).thenReturn(fakeTownHallBuildingDTO);
        Mockito.when(mapperService.convertBuildingToBuildingDTO(fakeUser.getKingdom().getBuildings().get(1))).thenReturn(fakeFarmBuildingDTO);

        Assertions.assertEquals(fakeTownHallBuildingDTO.getHp(), kingdomService.getBuildingsOfUser(request).get(0).getHp());
        Assertions.assertEquals(fakeFarmBuildingDTO.getType(), kingdomService.getBuildingsOfUser(request).get(1).getType());
    }

    @Test
    void getResourceByUserId_WithExistingUserById_ReturnsItsResources() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        ResourceDTO fakeGoldResourceDTO = beanFactory.getBean("fakeGoldResourceDTO", ResourceDTO.class);
        ResourceDTO fakeFoodResourceDTO = beanFactory.getBean("fakeFoodResourceDTO", ResourceDTO.class);
        List<Resource> fakeResourceList = fakeUser.getKingdom().getResources();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(globalUpdateService.updateResources(fakeUser.getKingdom().getResources(), fakeUser.getKingdom().getBuildings())).thenReturn(fakeUser.getKingdom().getResources());
        Mockito.when(mapperService.convertResourceToResourceDTO(fakeUser.getKingdom().getResources().get(0))).thenReturn(fakeGoldResourceDTO);
        Mockito.when(mapperService.convertResourceToResourceDTO(fakeUser.getKingdom().getResources().get(1))).thenReturn(fakeFoodResourceDTO);

        ResourceListDTO storedResources = kingdomService.getResourcesForAuthenticatedUser(request);

        Assertions.assertEquals(fakeResourceList.size(), storedResources.getResources().size());
        Assertions.assertEquals(fakeResourceList.get(0).getAmount(), storedResources.getResources().get(0).getAmount());
        Assertions.assertEquals(fakeResourceList.get(1).getAmount(), storedResources.getResources().get(1).getAmount());
        Assertions.assertEquals(fakeResourceList.get(0).getGeneration(), storedResources.getResources().get(0).getGeneration());
        Assertions.assertEquals(fakeResourceList.get(1).getGeneration(), storedResources.getResources().get(1).getGeneration());
        Assertions.assertEquals(fakeResourceList.get(0).getType(), storedResources.getResources().get(0).getType());
        Assertions.assertEquals(fakeUser.getKingdom().getResources().get(1).getType(), storedResources.getResources().get(1).getType());
    }

    @Test
    void getResourcesForAuthenticatedUser_WithNotExistingUserByUsername_ThrowsException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenThrow(new UserNotFoundException("Someone"));
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            kingdomService.getResourcesForAuthenticatedUser(request);
        });
    }

    @Test
    void getTownHallLevel_withKingdomHavingLevel1TownHall_Returns1() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);

        Assertions.assertEquals(1, kingdomService.getTownHallLevel(fakeUser.getKingdom()));
    }

    @Test
    void getTownHallLevel_withKingdomHavingNoTownHall_Returns0() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);

        Assertions.assertEquals(0, kingdomService.getTownHallLevel(fakeUser.getKingdom()));
    }

    @Test
    public void getKingdomById_WithInvalidKingdomID_ReturnsKingdomNotFoundException() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser);
        Mockito.when(kingdomRepository.getKingdomById(1L)).thenThrow(new KingdomNotFoundException());

        Optional<Kingdom> storedKingdomOptional = kingdomRepository.getKingdomById(2L);

        assertFalse(storedKingdomOptional.isPresent());
        Assertions.assertThrows(KingdomNotFoundException.class, () -> {
            kingdomService.getKingdomById(2L, request);
        });
    }

    @Test
    void getKingdomById_WithValidKingdomID_ReturnsValidKingdomDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(tribesUserDetailsService.getUserByUsernameFromRequest(request)).thenReturn(fakeUser.getKingdom().getUser());
        Mockito.when(kingdomRepository.getKingdomById(1L)).thenReturn(Optional.of(fakeUser.getKingdom()));

        Optional<Kingdom> storedKingdomOptional = kingdomRepository.getKingdomById(fakeUser.getKingdom().getId());

        assertTrue(storedKingdomOptional.isPresent());

        Kingdom storedKingdom = storedKingdomOptional.get();

        assertEquals(fakeUser.getKingdom().getId(), storedKingdom.getId());
        assertEquals(fakeUser.getKingdom().getName(), storedKingdom.getName());
    }
}