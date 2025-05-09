package com.example.playgame.exception;

public class BucketNullException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Bucket cannot be null.";

    public BucketNullException() {
        super(ERROR_MESSAGE);
    }
}

