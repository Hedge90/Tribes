package com.greenfoxacademy.springwebapp.exception;

public class InvalidTroopException extends RuntimeException {
    public InvalidTroopException() {
        super("Forbidden action");
    }
}
