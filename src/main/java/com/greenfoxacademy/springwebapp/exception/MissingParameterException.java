package com.greenfoxacademy.springwebapp.exception;

public class MissingParameterException extends RuntimeException {

    public MissingParameterException(String parameter) {
        super("Missing parameter(s): " + parameter);
    }
}
