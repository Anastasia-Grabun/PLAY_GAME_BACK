package com.example.playgame.exception;

/**
 * Исключение при неудачной аутентификации (неверный логин или пароль).
 */
public class AuthenticationFailedException extends RuntimeException {

    public static final String MESSAGE = "Неверный логин или пароль";

    public AuthenticationFailedException() {
        super(MESSAGE);
    }

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
