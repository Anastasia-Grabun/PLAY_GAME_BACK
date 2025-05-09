package com.example.playgame.exception.notfound;

public class DevelopersGamesNotFoundException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Developer with ID %d has no games.";

    public DevelopersGamesNotFoundException(Long developerId) {
        super(String.format(ERROR_MESSAGE, developerId));
    }
}

