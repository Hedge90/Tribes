package com.greenfoxacademy.springwebapp.exception;

public class KingdomAlreadyDefeatedException extends RuntimeException {
    public KingdomAlreadyDefeatedException(String kingdomName) {
        super(kingdomName + " has already been defeated!");
    }
}
