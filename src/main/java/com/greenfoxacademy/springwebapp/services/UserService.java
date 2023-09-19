package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.dtos.RegisteredUserDTO;
import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.entities.User;

import java.util.Optional;

public interface UserService {
    RegisteredUserDTO addNewUser(UserDTO userDTO);

    boolean isUserRegistered(String desiredUsername);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    void saveVerificationTokenForUser(UserDTO userDTO, String token);

    void verifyUser(String verificationToken);
}
