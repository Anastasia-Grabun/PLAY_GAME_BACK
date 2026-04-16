package com.example.playgame.service;

public interface EmailService {
    void sendVerificationEmail(String recipientEmail, String username, String verificationCode);
}
