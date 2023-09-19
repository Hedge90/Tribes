package com.greenfoxacademy.springwebapp.exception;

public class InvalidIDException extends RuntimeException {
    public InvalidIDException() {
        super("Invalid ID");
    }
}
