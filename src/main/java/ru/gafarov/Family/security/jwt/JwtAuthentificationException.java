package ru.gafarov.Family.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthentificationException extends AuthenticationException {

    public JwtAuthentificationException(String msg, Throwable cause) {
        super(msg, cause);
        System.out.println("эксепшн1");
    }

    public JwtAuthentificationException(String msg) {
        super(msg);
        System.out.println("эксепшн2");
    }
}
