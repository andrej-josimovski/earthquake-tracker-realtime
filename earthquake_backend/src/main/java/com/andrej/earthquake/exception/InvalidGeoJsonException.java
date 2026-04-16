package com.andrej.earthquake.exception;

public class InvalidGeoJsonException extends RuntimeException {
    public InvalidGeoJsonException(String message) {
        super("Invalid GeoJSON: " + message);
    }
}
