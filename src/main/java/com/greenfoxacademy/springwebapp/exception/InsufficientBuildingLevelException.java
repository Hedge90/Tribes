package com.greenfoxacademy.springwebapp.exception;

public class InsufficientBuildingLevelException extends RuntimeException {

    public InsufficientBuildingLevelException() {
        super("Cannot build buildings with higher level than the Townhall");
    }
}
