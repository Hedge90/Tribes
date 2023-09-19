package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.repositories.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationControllerTest {

    MockMvc mockMvc;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    UserRepository userRepository;

    @Autowired
    RegistrationControllerTest(MockMvc mockMvc, BeanFactory beanFactory, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
        this.userRepository = userRepository;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void addNewUser_AddNewUserWithGivenData_ReturnsNewUser() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(fakeUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(fakeUserDTO.getEmail()))
                .andExpect(jsonPath("$.kingdomId").isNumber());
    }


    @Test
    void addNewUser_WithLessThanEightCharacterPassword_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setPassword("1234567");
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Password must be 8 characters."));
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void addNewUser_WithExistingUsername_ReturnsExpectedErrorMessage() throws Exception {
        userRepository.save(beanFactory.getBean("fakeUser", User.class));
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    @Test
    void addNewUser_WithoutKingdomname_ReturnsExpectedErrorMessage() throws Exception {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setKingdomname(null);
        String userDTOJson = mapper.writeValueAsString(fakeUserDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDTOJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Kingdom is required."));
    }
}