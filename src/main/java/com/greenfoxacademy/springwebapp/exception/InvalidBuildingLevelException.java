package com.greenfoxacademy.springwebapp.exception;

public class InvalidBuildingLevelException extends RuntimeException {
    public InvalidBuildingLevelException() {
        super("Invalid building level");
    }
}
