package com.dmytrobilokha.tyde.infra.exception;

// TODO: consider making all top-level app exception unchecked
public class InternalApplicationException extends Exception {

    public InternalApplicationException(String message, Exception e) {
        super(message, e);
    }

    public InternalApplicationException(String message) {
        super(message);
    }

}
