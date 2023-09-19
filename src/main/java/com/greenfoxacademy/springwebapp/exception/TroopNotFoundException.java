package com.greenfoxacademy.springwebapp.exception;

public class TroopNotFoundException extends RuntimeException {
    public TroopNotFoundException() {
        super("Id not found");
    }
}
