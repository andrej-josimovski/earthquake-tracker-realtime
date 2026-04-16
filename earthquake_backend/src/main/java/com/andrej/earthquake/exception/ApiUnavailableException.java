package com.andrej.earthquake.exception;

public class ApiUnavailableException extends RuntimeException {
    public ApiUnavailableException() {
        super("USGS API is unavailable. Try again later.");
    }
}
