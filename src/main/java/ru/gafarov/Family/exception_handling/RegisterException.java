package ru.gafarov.Family.exception_handling;

public class RegisterException extends RuntimeException {
    public RegisterException(String message){
        super(message);
    }
}
