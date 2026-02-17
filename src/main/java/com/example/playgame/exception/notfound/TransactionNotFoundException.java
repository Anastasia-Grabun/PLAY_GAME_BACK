package com.example.playgame.exception.notfound;

public class TransactionNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Transaction";

    public TransactionNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public TransactionNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}
