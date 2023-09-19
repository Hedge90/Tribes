package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.*;
import com.greenfoxacademy.springwebapp.entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MapperServiceImplTest {

    MapperServiceImpl mapperServiceImpl;

    BeanFactory beanFactory;

    @Autowired
    MapperServiceImplTest(MapperServiceImpl mapperServiceImpl, BeanFactory beanFactory) {
        this.mapperServiceImpl = mapperServiceImpl;
        this.beanFactory = beanFactory;
    }

    @Test
    void convertUserToUserDTO_FromValidUser_ReturnsCorrectDTO() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        UserDTO userDTO = mapperServiceImpl.convertUserToUserDTO(fakeUser);

        Assertions.assertEquals(fakeUser.getUsername(), userDTO.getUsername());
        Assertions.assertEquals(fakeUser.getEmail(), userDTO.getEmail());
        Assertions.assertEquals(fakeUser.getPassword(), userDTO.getPassword());
        Assertions.assertEquals(fakeUser.getKingdom().getName(), userDTO.getKingdomname());
    }

    @Test
    void convertBuildingToBuildingDTO_FromValidBuilding_ReturnsCorrectDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        Building fakeBuilding = fakeUser.getKingdom().getBuildings().get(0);
        BuildingDTO buildingDTO = mapperServiceImpl.convertBuildingToBuildingDTO(fakeUser.getKingdom().getBuildings().get(0));

        Assertions.assertEquals(fakeBuilding.getLevel(), buildingDTO.getLevel());
        Assertions.assertEquals(fakeBuilding.getHp(), buildingDTO.getHp());
        Assertions.assertEquals(fakeBuilding.getType(), buildingDTO.getType());
    }

    @Test
    void convertKingdomToKingdomDTO_FromValidKingdom_ReturnsCorrectDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUserWithIds", User.class);
        KingdomDTO kingdomDTO = mapperServiceImpl.convertKingdomToKingdomDTO(fakeUser.getKingdom());

        Assertions.assertEquals(fakeUser.getKingdom().getName(), kingdomDTO.getName());
        Assertions.assertEquals(fakeUser.getKingdom().getId(), kingdomDTO.getUserId());
        Assertions.assertEquals(fakeUser.getKingdom().getLocation().getX(), kingdomDTO.getLocation().getX());
        Assertions.assertEquals(fakeUser.getKingdom().getBuildings().get(0).getType(), kingdomDTO.getBuildings().get(0).getType());
    }

    @Test
    void convertUserDTOtoUser_FromValidUserDTO_ReturnsCorrectUser() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        User user = mapperServiceImpl.convertUserDTOtoUser(fakeUserDTO);

        Assertions.assertEquals(fakeUserDTO.getUsername(), user.getUsername());
        Assertions.assertEquals(fakeUserDTO.getEmail(), user.getEmail());
        Assertions.assertEquals(fakeUserDTO.getPassword(), user.getPassword());
        Assertions.assertEquals(fakeUserDTO.getKingdomname(), user.getKingdom().getName());
    }

    @Test
    void convertResourceToResourceDTO_FromValidResource_ReturnsCorrectDTO() {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        Resource fakeResource = fakeUser.getKingdom().getResources().get(1);
        ResourceDTO resourceDTO = mapperServiceImpl.convertResourceToResourceDTO(fakeUser.getKingdom().getResources().get(1));

        Assertions.assertEquals(fakeResource.getType(), resourceDTO.getType());
        Assertions.assertEquals(fakeResource.getAmount(), resourceDTO.getAmount());
        Assertions.assertEquals(fakeResource.getGeneration(), resourceDTO.getGeneration());
        Assertions.assertEquals(fakeResource.getUpdatedAt(), resourceDTO.getUpdatedAt());
    }
}