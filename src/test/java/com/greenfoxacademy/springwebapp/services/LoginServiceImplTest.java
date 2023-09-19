package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.entities.User;
import com.greenfoxacademy.springwebapp.security.AuthenticationRequest;
import com.greenfoxacademy.springwebapp.security.JwtUtil;
import com.greenfoxacademy.springwebapp.security.TribesUserDetailsService;
import com.greenfoxacademy.springwebapp.security.UserDetailsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LoginServiceImplTest {

    TribesUserDetailsService tribesUserDetailsService;

    JwtUtil jwtTokenUtil;

    UserService userService;

    AuthenticationManager authenticationManager;

    LoginServiceImpl loginServiceImpl;

    BeanFactory beanFactory;

    @Autowired
    LoginServiceImplTest(BeanFactory beanFactory) {
        tribesUserDetailsService = Mockito.mock(TribesUserDetailsService.class);
        jwtTokenUtil = Mockito.mock(JwtUtil.class);
        userService = Mockito.mock(UserService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        loginServiceImpl = new LoginServiceImpl(tribesUserDetailsService, userService, jwtTokenUtil, authenticationManager);
        this.beanFactory = beanFactory;
    }

    @Test
    void createAuthenticationToken_WithValidRequest_ReturnsValidString() {
        User fakeUser = beanFactory.getBean("fakeUser", User.class);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(fakeUser.getUsername(), fakeUser.getPassword());
        UserDetailsImpl userDetails = tribesUserDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        String token = "ljaéal4jlkdfjélkjw4éltkjslgkjsfélgkjeéljg élskjgéslfdk jgsélkjgsflédkgjsfélsfdgkgj";
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        Assertions.assertTrue(token == loginServiceImpl.createAuthenticationToken(authenticationRequest));
    }
}