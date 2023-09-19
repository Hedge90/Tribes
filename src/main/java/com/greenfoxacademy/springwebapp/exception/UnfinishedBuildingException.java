package com.greenfoxacademy.springwebapp.exception;

public class UnfinishedBuildingException extends RuntimeException {
    public UnfinishedBuildingException() {
        super("The update of the building is not ready!");
    }
}
