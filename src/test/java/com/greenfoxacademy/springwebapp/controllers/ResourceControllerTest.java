package com.greenfoxacademy.springwebapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.springwebapp.entities.Resource;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResourceControllerTest {
    MockMvc mockMvc;

    UserRepository userRepository;

    JwtUtil jwtUtil;

    ObjectMapper mapper;

    BeanFactory beanFactory;

    @Autowired
    ResourceControllerTest(MockMvc mockMvc, UserRepository userRepository, JwtUtil jwtUtil, BeanFactory beanFactory) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        mapper = new ObjectMapper();
        this.beanFactory = beanFactory;
    }

    @Test
    @Sql("/db/test/clear_tables.sql")
    void getResourceInformation_WithResources_ReturnsCorrectResourceListDTO() throws Exception {
        User fakeUser = beanFactory.getBean("fullFakeUser", User.class);
        List<Resource> fakeResourceList = fakeUser.getKingdom().getResources();
        userRepository.save(fakeUser);

        MockHttpServletRequestBuilder requestBuilder = get("/kingdom/resources")
                .header("Authorization", "Bearer " + jwtUtil.generateToken(new UserDetailsImpl(fakeUser)));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resources[0].amount").value(fakeResourceList.get(0).getAmount()))
                .andExpect(jsonPath("$.resources[1].amount").value(fakeResourceList.get(1).getAmount()));
    }
}
