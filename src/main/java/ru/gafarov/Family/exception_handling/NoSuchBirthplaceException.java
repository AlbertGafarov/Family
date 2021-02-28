package ru.gafarov.Family.exception_handling;

public class NoSuchBirthplaceException extends RuntimeException {
    public NoSuchBirthplaceException(String message) {
        super(message);
    }
}
