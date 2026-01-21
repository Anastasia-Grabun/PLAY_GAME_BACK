package com.example.playgame.exception.notfound;

public class EntityNotFoundException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Entity %s with ID %s not found.";
    private static final String ERROR_MESSAGE_STR = "Entity %s not found. %s";

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format(ERROR_MESSAGE, entityName, id));
    }

    public EntityNotFoundException(String entityName, String message) {
        super(String.format(ERROR_MESSAGE_STR, entityName, message));
    }
}
