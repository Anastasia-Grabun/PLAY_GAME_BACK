package com.example.playgame.service.impl;

import com.example.playgame.entity.Credential;
import com.example.playgame.repository.CredentialRepository;
import com.example.playgame.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final JwtService jwtService;
    private final CredentialRepository credentialRepository;

    public Long extractAccountId(String authHeader) {
        String token = parseToken(authHeader);
        String login = jwtService.extractLogin(token);

        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Credential not found"));

        return credential.getAccount().getId();
    }

    private String parseToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}

