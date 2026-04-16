package com.andrej.earthquake.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EarthquakeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EarthquakeNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(ApiUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleApiUnavailable(ApiUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "API Unavailable", ex.getMessage());
    }

    @ExceptionHandler(InvalidGeoJsonException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidGeoJson(InvalidGeoJsonException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid GeoJSON", ex.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseError(DatabaseException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error", ex.getMessage());
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDate(InvalidDateFormatException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Date Format", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
