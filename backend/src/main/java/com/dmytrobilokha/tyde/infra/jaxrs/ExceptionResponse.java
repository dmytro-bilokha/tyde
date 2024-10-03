package com.dmytrobilokha.tyde.infra.jaxrs;

public class ExceptionResponse {

    private final String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
