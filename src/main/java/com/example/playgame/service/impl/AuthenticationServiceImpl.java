package com.example.playgame.service.impl;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;
import com.example.playgame.entity.Account;
import com.example.playgame.entity.Credential;
import com.example.playgame.entity.Role;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.CredentialRepository;
import com.example.playgame.repository.RoleRepository;
import com.example.playgame.security.CustomUserDetails;
import com.example.playgame.security.CustomUserServiceImpl;
import com.example.playgame.security.jwt.JwtService;
import com.example.playgame.service.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserServiceImpl customUserService;

    @Override
    @Transactional
    public JwtResponseDto login(LoginDto loginDto) {
        validateCredentials(loginDto);

        UserDetails user = customUserService.loadUserByUsername(loginDto.getLogin());

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtService.generateToken(
                Map.of("role", roles),
                user
        );

        return new JwtResponseDto(token);
    }

    @Override
    @Transactional
    public JwtResponseDto register(RegisterDto registerDto) {
        validateRegistration(registerDto);

        Account newAccount = new Account();
        newAccount.setUsername(registerDto.getUsername());
        newAccount.setEmail(registerDto.getEmail());

        Credential credential = new Credential();
        credential.setLogin(registerDto.getLogin());
        credential.setPassword(passwordEncoder.encode(registerDto.getPassword()));


        Role role = roleRepository.findById(2L)
                .orElseThrow();
        credential.setRoles(Collections.singletonList(role));

        accountRepository.save(newAccount);
        credential.setAccount(newAccount);
        credentialRepository.save(credential);

        String token = jwtService.generateToken(
                Map.of("role", credential.getRoles().get(0).getName().name()),
                new CustomUserDetails(credential)
        );

        return new JwtResponseDto(token);
    }

    private void validateCredentials(LoginDto loginDto){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword())
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: " + loginDto.getLogin(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        log.info("Authentication successful");
    }

    private void validateRegistration(RegisterDto registerDto) {
        if(accountRepository.existsByUsername(registerDto.getUsername())){
            throw new IllegalArgumentException("Account with such username has already exists");
        }
    }
}