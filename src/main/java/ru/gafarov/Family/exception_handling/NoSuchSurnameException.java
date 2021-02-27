package ru.gafarov.Family.exception_handling;

public class NoSuchSurnameException extends RuntimeException {
    public NoSuchSurnameException(String message) {
        super(message);
    }
}

