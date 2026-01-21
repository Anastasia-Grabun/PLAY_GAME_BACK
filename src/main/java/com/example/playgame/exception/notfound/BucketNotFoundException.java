package com.example.playgame.exception.notfound;

public class BucketNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Bucket";

    public BucketNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION , id);
    }

    public BucketNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}
