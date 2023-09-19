package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.ErrorDTO;
import com.greenfoxacademy.springwebapp.exception.handlers.GlobalExceptionHandler;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void userNotFoundException_WhenGiveUserName_ReturnGivenUserNameAndMessage() {
        String username = "fakeUser";
        ErrorDTO errorDTO = new ErrorDTO("error", "User with name " + username + " is not found");
        ErrorDTO resultErrorDTO = (ErrorDTO) globalExceptionHandler.handleUserNotFoundException(new UserNotFoundException(username)).getBody();
        assertEquals(errorDTO.getMessage(), resultErrorDTO.getMessage());
    }
}
