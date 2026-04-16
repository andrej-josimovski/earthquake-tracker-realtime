package com.andrej.earthquake.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException() {
        super("Invalid date format. Use: yyyy-MM-ddTHH:mm:ss (example: 2026-04-15T10:30:00)");
    }
}
