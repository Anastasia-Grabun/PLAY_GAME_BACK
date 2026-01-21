package com.example.playgame.exception;

public class AccountNullException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Account cannot be null.";

    public AccountNullException() {
        super(ERROR_MESSAGE);
    }
}

