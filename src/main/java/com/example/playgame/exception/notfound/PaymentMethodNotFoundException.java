package com.example.playgame.exception.notfound;

public class PaymentMethodNotFoundException extends EntityNotFoundException {
    private static final String EXCEPTION_EXTENSION_DEFINITION = "Payment Method";

    public PaymentMethodNotFoundException(Long id) {
        super(EXCEPTION_EXTENSION_DEFINITION, id);
    }
}
