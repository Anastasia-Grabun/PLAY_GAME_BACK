package com.example.playgame.exception.notfound;

public class CurrencyNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Currency";

    public CurrencyNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public CurrencyNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}
