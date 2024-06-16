package com.dmytrobilokha.tyde.infra.exception;

// TODO: consider making all these exceptions unchecked
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Exception e) {
        super(message, e);
    }

}
