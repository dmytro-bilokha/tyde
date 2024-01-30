package com.dmytrobilokha.tyde.infra.db;

public class DbException extends RuntimeException {

    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Exception e) {
        super(message, e);
    }

}
