package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingLevelDTO;
import com.greenfoxacademy.springwebapp.dtos.BuildingTypeDTO;
import com.greenfoxacademy.springwebapp.entities.Building;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BuildingControllerTest {


    MockMvc mockMvc;

    UserRepository userRepository;

    JwtUtil jwtUtil;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    GameLogicConfiguration configuration;

    @Autowired
    BuildingControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory beanFactory, GameLogicConfiguration configuration) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
        this.configuration = configuration;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getBuildings_WithExistingBuildings_ReturnsBuildings() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        List<Building> fakeBuildingList = fakeUser.getKingdom().getBuildings();
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/buildings")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        // Act + Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildings.size()").value(fakeBuildingList.size()))
                .andExpect(jsonPath("$.buildings[3].type").value(fakeBuildingList.get(3).getType().toString()))
                .andExpect(jsonPath("$.buildings[0].type").value(fakeBuildingList.get(0).getType().toString()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getBuildings_WithNotExistingBuildings_ReturnsEmptyArray() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/buildings")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buildings.size()").value(fakeUser.getKingdom().getBuildings().size()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getBuilding_WithValidUserAndMatchingId_ReturnsOkStatusAndCorrectBuildingDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        Building fakeBuilding = fakeUser.getKingdom().getBuildings().get(0);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/buildings/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(fakeBuilding.getLevel()))
                .andExpect(jsonPath("$.hp").value(fakeBuilding.getHp()))
                .andExpect(jsonPath("$.type").value(fakeBuilding.getType().toString()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getBuilding_WithNoMatchingBuildingId_ThrowsBuildingNotFoundException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/buildings/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Id not found"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getBuilding_WithBuildingNotValidForUser_ThrowsInvalidBuildingException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        User otherFakeUser = beanFactory.getBean("fullFakeUser", User.class);
        otherFakeUser.setUsername("otherFakeUser");
        userRepository.save(fakeUser);
        userRepository.save(otherFakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/buildings/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Forbidden action"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void createBuilding_WithProperTypeAndEnoughResources_CreatesCorrectBuildingAndReturnsValidBuildingDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);

        String buildingTypeDTOJson = mapper.writeValueAsString(new BuildingTypeDTO("mine", 1));
        MockHttpServletRequestBuilder requestBuilder = post("/kingdom/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingTypeDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.hp").value(configuration.getMineHp()))
                .andExpect(jsonPath("$.type").value("MINE"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithValidLevelAndEnoughResources_UpdateCorrectBuildingAndReturnsValidBuildingDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        Building fakeBuilding = fakeUser.getKingdom().getBuildings().get(0);
        userRepository.save(fakeUser);

        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(2));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(fakeBuilding.getLevel()))
                .andExpect(jsonPath("$.hp").value(fakeBuilding.getHp()))
                .andExpect(jsonPath("$.type").value(fakeBuilding.getType().toString()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithValidLevelAndNotEnoughResources_ThrowsNotEnoughResourcesException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        fakeUser.getKingdom().getResources().get(0).setAmount(0);
        userRepository.save(fakeUser);

        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(2));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Not enough resources"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithInvalidLevelAndEnoughResources_ThrowsInvalidBuildingLevelException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);

        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(99));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid building level"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithoutBuilding_ThrowsBuildingNotFoundException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(2));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Id not found"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithValidLevelAndEnoughResourcesButTownhallLevelIsEqual_ThrowsInsufficientBuildingLevelException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);

        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(2));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Cannot build buildings with higher level than the Townhall"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeBuilding_WithBuildingUnderUpgrade_ThrowsNotFinishBuildingException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        fakeUser.getKingdom().getBuildings().get(0).setUnderUpdate(true);
        fakeUser.getKingdom().getBuildings().get(0).setFinishedAt(fakeUser.getKingdom().getBuildings().get(0).getFinishedAt() + 999999);
        userRepository.save(fakeUser);
        
        String buildingLevelDTOJson = mapper.writeValueAsString(new BuildingLevelDTO(2));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/buildings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingLevelDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("The update of the building is not ready!"));
    }
}
