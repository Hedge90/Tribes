package com.greenfoxacademy.springwebapp.controllers;

import com.greenfoxacademy.springwebapp.dtos.ErrorDTO;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.AuthenticationResponse;
import com.greenfoxacademy.springwebapp.services.GeneralUtility;
import com.greenfoxacademy.springwebapp.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        String invalidParameter = "";
        if (GeneralUtility.isEmptyOrNull(authenticationRequest.getUsername())) {
            invalidParameter = "Username";
        } else if (GeneralUtility.isEmptyOrNull(authenticationRequest.getPassword())) {
            invalidParameter = "Password";
        }
        if (!invalidParameter.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("error", invalidParameter + " is required."));
        }
        loginService.authenticate(authenticationRequest);
        loginService.checkEmailVerification(authenticationRequest.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(new AuthenticationResponse("ok", loginService.createAuthenticationToken(authenticationRequest)));
    }
}