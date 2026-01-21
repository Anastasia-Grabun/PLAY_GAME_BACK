package com.example.playgame.controllers;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;
import com.example.playgame.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/authorization")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtResponseDto processLogin(@Valid @RequestBody LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }

    @PostMapping("/register")
    public JwtResponseDto processRegistration(@Valid @RequestBody RegisterDto registerDto) {
        return authenticationService.register(registerDto);
    }
}
