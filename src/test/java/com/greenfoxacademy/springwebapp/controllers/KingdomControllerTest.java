package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.dtos.KingdomNameDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class KingdomControllerTest {

    MockMvc mockMvc;

    UserRepository userRepository;
    JwtUtil jwtUtil;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    @Autowired
    KingdomControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory  beanFactory) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void setKingdomName_WithValidKingdomNameDTO_ReturnsValidKingdomDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);

        KingdomNameDTO kingdomNameDTO = new KingdomNameDTO("ValidKingdomName");
        String kingdomDTOJson = mapper.writeValueAsString(kingdomNameDTO);
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(kingdomDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(kingdomNameDTO.getName()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void setKingdomName_WithInvalidKingdomNameDTO_ReturnsBadRequest() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        String kingdomDTOJson = mapper.writeValueAsString(new KingdomNameDTO(""));
        MockHttpServletRequestBuilder requestBuilder = put("/kingdom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(kingdomDTOJson)
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getKingdomInformation_WithValidKingdom_ReturnsValidKingdomDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(fakeUser.getKingdom().getName()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getKingdomById_WithValidID_ReturnsOkAndKingdomDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        mockMvc.perform(get("/kingdom/{id}", 1)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(fakeUser.getKingdom().getId()))
                .andExpect(jsonPath("$.name").value(fakeUser.getKingdom().getName()));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getKingdomInformation_WithInvalidKingdom_ThrowKingdomNotFoundException() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        fakeUser.setKingdom(null);
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Id not found"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getKingdomById_WithInvalidID_ReturnsBadRequestAndErrorDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        mockMvc.perform(get("/kingdom/{id}", 0)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid ID"));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getKingdomById_WithNonExistentID_ReturnsNotFoundAndErrorDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        userRepository.save(fakeUser);

        UserDetailsImpl userDetails = new UserDetailsImpl(fakeUser);

        mockMvc.perform(get("/kingdom/{id}", 100)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Id not found"));
    }

}
