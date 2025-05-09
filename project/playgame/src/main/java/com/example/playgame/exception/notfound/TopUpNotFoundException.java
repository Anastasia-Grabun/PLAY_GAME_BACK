package com.example.playgame.exception.notfound;

public class TopUpNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "TopUp";

    public TopUpNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public TopUpNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}
