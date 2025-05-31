package com.example.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends Exception {
    private final HttpStatus status;

    public ValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
