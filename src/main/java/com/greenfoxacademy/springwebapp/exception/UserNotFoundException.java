package com.greenfoxacademy.springwebapp.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Username or password is incorrect.");
    }

    public UserNotFoundException(String username) {
        super("User with name " + username + " is not found");
    }

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " is not found");
    }
}
