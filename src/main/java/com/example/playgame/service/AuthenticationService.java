package com.example.playgame.service;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;
import com.example.playgame.dto.VerifyEmailCodeDto;

public interface AuthenticationService {
    JwtResponseDto login(LoginDto loginDto);
    void register(RegisterDto registerDto);
    JwtResponseDto verifyEmailCode(VerifyEmailCodeDto verifyEmailCodeDto);
    void resendVerificationEmail(String login);
}