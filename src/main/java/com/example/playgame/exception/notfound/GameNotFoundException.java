package com.example.playgame.exception.notfound;

public class GameNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Game";

    public GameNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public GameNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}