package com.greenfoxacademy.springwebapp.security;

import com.greenfoxacademy.springwebapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private UserRepository userRepository;

    private JwtRequestFilter jwtRequestFilter;

    private JwtUtil jwtUtil;

    @Autowired
    public SecurityConfiguration(UserRepository userRepository, JwtRequestFilter jwtRequestFilter, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new TribesUserDetailsService(userRepository, jwtUtil);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "/registration", "/login", "/register", "/verify-email", "/your-kingdom", "/access-denied").permitAll()
                        .antMatchers("/js/**", "/css/**", "/img/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .csrf(csrf -> csrf.disable())
                .formLogin(login -> login.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
