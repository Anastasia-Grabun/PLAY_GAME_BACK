package com.example.playgame.exception.notfound;

public class PurchaseNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Purchase";

    public PurchaseNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION , id);
    }

    public PurchaseNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}

