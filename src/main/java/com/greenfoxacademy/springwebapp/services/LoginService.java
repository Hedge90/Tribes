package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;

public interface LoginService {

    String createAuthenticationToken(AuthenticationRequest authenticationRequest);

    void authenticate(AuthenticationRequest authenticationRequest);

    void checkEmailVerification(String username);
}
