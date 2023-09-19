package com.greenfoxacademy.springwebapp.exception;

public class NotValidAcademyException extends RuntimeException {
    public NotValidAcademyException() {
        super("Not a valid academy id");
    }
}
