package com.example.playgame.exception.notfound;

public class AccountNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Account";

    public AccountNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public AccountNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}