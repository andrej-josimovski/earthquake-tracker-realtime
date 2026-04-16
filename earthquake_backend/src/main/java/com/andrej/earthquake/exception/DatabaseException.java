package com.andrej.earthquake.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super("Database error: " + message);
    }
}
