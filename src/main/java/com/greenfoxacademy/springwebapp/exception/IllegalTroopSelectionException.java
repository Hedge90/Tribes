package com.greenfoxacademy.springwebapp.exception;

public class IllegalTroopSelectionException extends RuntimeException {

    public IllegalTroopSelectionException() {
        super("The list of attacking troops contains one or more troop that is dead or does not belong to your kingdom!");
    }
}
