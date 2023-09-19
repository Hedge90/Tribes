package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.ErrorDTO;
import com.greenfoxacademy.springwebapp.dtos.RegisteredUserDTO;
import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.emailservice.EmailService;
import com.greenfoxacademy.springwebapp.services.GeneralUtility;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
public class RegistrationController {
    private UserService userService;

    private EmailService emailService;

    @Autowired
    public RegistrationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        String parameter = "";
        if (GeneralUtility.isEmptyOrNull(userDTO.getUsername())) {
            parameter = "Username";
        } else if (GeneralUtility.isEmptyOrNull(userDTO.getEmail())) {
            parameter = "Email";
        } else if (GeneralUtility.isEmptyOrNull(userDTO.getPassword())) {
            parameter = "Password";
        } else if (GeneralUtility.isEmptyOrNull(userDTO.getKingdomname())) {
            parameter = "Kingdom";
        }

        if (parameter.length() > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("error", parameter + " is required."));
        }

        if (!GeneralUtility.hasLessCharactersThan(userDTO.getPassword(), 8)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorDTO("error", "Password must be 8 characters."));
        }

        if (userService.isUserRegistered(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("error", "Username is already taken"));
        }
        RegisteredUserDTO registeredUserDTO = userService.addNewUser(userDTO);
        try {
            emailService.sendVerificationRequest(userDTO);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUserDTO);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmailAddress(@RequestParam String token) {
        userService.verifyUser(token);
        return ResponseEntity.status(HttpStatus.CREATED).body("Your email has been verified");
    }
}
