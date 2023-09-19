package com.greenfoxacademy.springwebapp.exception;

public class BuildingNotFoundException extends RuntimeException {

    public BuildingNotFoundException() {
        super("Id not found");
    }
}