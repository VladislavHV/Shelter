package com.example.Shelter.exception;

public class ReportValidationException extends RuntimeException {
    public ReportValidationException(String message) {
        super(message);
    }
}