package com.example.playgame.exception.notfound;

public class CredentialNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Credential";

    public CredentialNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }

    public CredentialNotFoundException(String message) {
        super(EXCEPTION_EXTENSION_DEFINITION, message);
    }
}