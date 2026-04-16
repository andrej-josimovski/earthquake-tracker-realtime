package com.andrej.earthquake.exception;

public class EarthquakeNotFoundException extends RuntimeException {
    public EarthquakeNotFoundException(Long id) {
        super("Earthquake with id " + id + " not found");
    }
}
