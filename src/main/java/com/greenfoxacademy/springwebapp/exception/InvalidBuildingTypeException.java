package com.greenfoxacademy.springwebapp.exception;

public class InvalidBuildingTypeException extends RuntimeException {

    public InvalidBuildingTypeException() {
        super("Invalid building type");
    }

}
