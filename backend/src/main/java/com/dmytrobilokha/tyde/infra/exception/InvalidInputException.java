package com.dmytrobilokha.tyde.infra.exception;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Exception e) {
        super(message, e);
    }

}
