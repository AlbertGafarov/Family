package ru.gafarov.Family.exception_handling;

public class NoSuchHumanException extends RuntimeException{
    public NoSuchHumanException(String message) {
        super(message);
    }
}
