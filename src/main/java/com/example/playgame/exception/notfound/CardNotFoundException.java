package com.example.playgame.exception.notfound;

public class CardNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Card";

    public CardNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }
}
