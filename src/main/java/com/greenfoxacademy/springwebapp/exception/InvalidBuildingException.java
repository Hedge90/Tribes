package com.greenfoxacademy.springwebapp.exception;

public class InvalidBuildingException extends RuntimeException {

    public InvalidBuildingException() {
        super("Forbidden action");
    }

    public InvalidBuildingException(String message) {
        super(message);
    }
}
