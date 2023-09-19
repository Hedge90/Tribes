package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.emailservice.EmailService;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
class RegistrationControllerUnitTests {

    UserService userService;

    EmailService emailService;

    RegistrationController registrationController;

    BeanFactory beanFactory;

    @Autowired
    RegistrationControllerUnitTests(BeanFactory beanFactory) {
        userService = Mockito.mock(UserService.class);
        emailService = Mockito.mock(EmailService.class);
        registrationController = new RegistrationController(userService, emailService);
        this.beanFactory = beanFactory;
    }

    @Test
    void createUser_WithEmptyUsername_ReturnsBadRequest() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setUsername("");

        ResponseEntity<?> response = registrationController.createUser(fakeUserDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUser_WithShortPassword_ReturnsNotAcceptableStatus() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        fakeUserDTO.setPassword("short");
        Mockito.when(userService.isUserRegistered(anyString())).thenReturn(false);

        ResponseEntity<?> response = registrationController.createUser(fakeUserDTO);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    @Test
    void createUser_WithAlreadyRegisteredUsername_ReturnsConflictStatus() {
        UserDTO fakeUserDTO = beanFactory.getBean("fakeUserDTO", UserDTO.class);
        Mockito.when(userService.isUserRegistered(anyString())).thenReturn(true);

        ResponseEntity<?> response = registrationController.createUser(fakeUserDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}