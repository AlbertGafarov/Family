package ru.gafarov.Family.exception_handling;

public class PhotoFileException extends RuntimeException{
    public PhotoFileException(String message) {
        super(message);
    }
}
