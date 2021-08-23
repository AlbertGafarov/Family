package ru.gafarov.Family.exception_handling;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
