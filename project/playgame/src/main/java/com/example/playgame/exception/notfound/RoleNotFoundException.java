package com.example.playgame.exception.notfound;

public class RoleNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Role";

    public RoleNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }
}
