package com.greenfoxacademy.springwebapp.exception;

public class KingdomNotFoundException extends RuntimeException {
    public KingdomNotFoundException() {
        super("Id not found");
    }
}
