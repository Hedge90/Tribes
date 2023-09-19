package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.configurations.GameLogicConfiguration;
import com.greenfoxacademy.springwebapp.dtos.BuildingIDDTO;
import com.greenfoxacademy.springwebapp.dtos.TroopListDTO;
import com.greenfoxacademy.springwebapp.entities.Troop;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TroopControllerTest {

    MockMvc mockMvc;

    UserRepository userRepository;

    JwtUtil jwtUtil;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    GameLogicConfiguration configuration;

    @Autowired
    TroopControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory beanFactory, GameLogicConfiguration configuration) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
        this.configuration = configuration;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void postTroop_WithCorrectDTOAndUserHavingAcademyWithMatchingId_ReturnsCorrectTroopDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);
        int academyLevel = fakeUser.getKingdom().getBuildings().get(3).getLevel();
        Troop fakeTroop = fakeUser.getKingdom().getTroops().get(0);
        String buildingIDDTOJson = mapper.writeValueAsString(new BuildingIDDTO(fakeUser.getKingdom().getBuildings().get(3).getId()));
        MockHttpServletRequestBuilder requestBuilder = post("/kingdom/troops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingIDDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(academyLevel))
                .andExpect(jsonPath("$.hp").value(fakeTroop.getHp()))
                .andExpect(jsonPath("$.attack").value(fakeTroop.getAttack()))
                .andExpect(jsonPath("$.defence").value(fakeTroop.getDefence()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void postTroop_WithInvalidBuildingID_ThrowsNotValidAcademyException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(1L);
        String buildingIDDTOJson = mapper.writeValueAsString(buildingIDDTO);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = post("/kingdom/troops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingIDDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Not a valid academy id"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void postTroop_WithInsufficientGold_ThrowsNotEnoughResourcesException() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        fakeUser.getKingdom().getResources().get(0).setAmount(0);
        BuildingIDDTO buildingIDDTO = new BuildingIDDTO(4L);
        String buildingIDDTOJson = mapper.writeValueAsString(buildingIDDTO);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = post("/kingdom/troops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildingIDDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Not enough resources"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getTroopsInformation_WithoutTroops_ReturnsEmptyTroopsListDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        TroopListDTO emptyTroopListDTO = new TroopListDTO();
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/troops")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.troops").value(emptyTroopListDTO.getTroops()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getTroopsInformation_WithTroops_ReturnsCorrectTroopsListDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        List<Troop> fakeTroopList = fakeUser.getKingdom().getTroops();
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/troops")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.troops.size()").value(fakeTroopList.size()))
                .andExpect(jsonPath("$.troops[1].level").value(fakeTroopList.get(0).getLevel()))
                .andExpect(jsonPath("$.troops[1].hp").value(fakeTroopList.get(0).getHp()))
                .andExpect(jsonPath("$.troops[1].attack").value(fakeTroopList.get(0).getAttack()))
                .andExpect(jsonPath("$.troops[1].defence").value(fakeTroopList.get(0).getDefence()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getTroop_WithTroopAndCorrectId_ReturnsCorrectTroopDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        Troop fakeTroop = fakeUser.getKingdom().getTroops().get(0);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/troops/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(fakeTroop.getLevel()))
                .andExpect(jsonPath("$.hp").value(fakeTroop.getHp()))
                .andExpect(jsonPath("$.attack").value(fakeTroop.getAttack()))
                .andExpect(jsonPath("$.defence").value(fakeTroop.getDefence()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getTroop_WithInvalidTroopId_ThrowsTroopNotFoundException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/troops/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Id not found"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getTroop_WithWrongUsersTroopId_ThrowsInvalidTroopException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        User otherFakeUser = beanFactory.getBean("fullFakeUser", User.class);
        otherFakeUser.setUsername("otherFakeUser");
        userRepository.save(fakeUser);
        userRepository.save(otherFakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/troops/{id}", 1)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Forbidden action"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void upgradeTroopById_WithCorrectDTOAndUserHavingAcademyWithMatchingId_ReturnsCorrectTroopDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        BuildingIDDTO fakeBuildingIdDTO = new BuildingIDDTO(4L);
        Troop fakeTroop = fakeUser.getKingdom().getTroops().get(0);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = put("/kingdom/troops/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(fakeBuildingIdDTO))
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(fakeTroop.getId()))
                .andExpect(jsonPath("$.hp").value(fakeTroop.getHp()))
                .andExpect(jsonPath("$.attack").value(fakeTroop.getAttack()))
                .andExpect(jsonPath("$.defence").value(fakeTroop.getDefence()))
                .andExpect(jsonPath("$.finishedAt").isNumber());
    }
}