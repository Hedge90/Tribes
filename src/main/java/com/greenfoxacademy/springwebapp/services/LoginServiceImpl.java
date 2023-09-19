package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.exception.EmailNotVerifiedException;
import com.greenfoxacademy.springwebapp.exception.UserNotFoundException;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import com.greenfoxacademy.springwebapp.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private TribesUserDetailsService tribesUserDetailsService;

    private UserService userService;

    private JwtUtil jwtTokenUtil;

    private AuthenticationManager authenticationManager;

    @Autowired
    public LoginServiceImpl(TribesUserDetailsService tribesUserDetailsService, UserService userService, JwtUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.tribesUserDetailsService = tribesUserDetailsService;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String createAuthenticationToken(AuthenticationRequest authenticationRequest) {
        UserDetailsImpl userDetails = tribesUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public void authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public void checkEmailVerification(String username) {
        if (!userService.findUserByUsername(username).get().isVerified()) {
            throw new EmailNotVerifiedException();
        }
    }
}
