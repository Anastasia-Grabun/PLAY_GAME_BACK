package com.example.playgame.service;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;

public interface AuthenticationService {
    JwtResponseDto login(LoginDto loginDto);
    JwtResponseDto register(RegisterDto registerDto);
}