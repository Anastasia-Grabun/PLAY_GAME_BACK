package com.example.playgame.exception.notfound;

public class GenreNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Genre";

    public GenreNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public GenreNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}
