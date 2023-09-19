package com.greenfoxacademy.springwebapp.exception;

public class NotEnoughResourcesException extends RuntimeException {

    public NotEnoughResourcesException() {
        super("Not enough resources");
    }
}
