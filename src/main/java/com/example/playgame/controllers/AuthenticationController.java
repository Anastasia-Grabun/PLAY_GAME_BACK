package com.example.playgame.controllers;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;
import com.example.playgame.dto.VerifyEmailCodeDto;
import com.example.playgame.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<Map<String, String>> processRegistration(@Valid @RequestBody RegisterDto registerDto) {
        authenticationService.register(registerDto);
        return ResponseEntity.ok(Map.of("message", "На ваш email отправлен код подтверждения."));
    }

    @PostMapping("/verify-code")
    public JwtResponseDto verifyCode(@Valid @RequestBody VerifyEmailCodeDto verifyEmailCodeDto) {
        return authenticationService.verifyEmailCode(verifyEmailCodeDto);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestParam("login") String login) {
        authenticationService.resendVerificationEmail(login);
        return ResponseEntity.ok(Map.of("message", "Письмо с подтверждением отправлено повторно."));
    }
}
