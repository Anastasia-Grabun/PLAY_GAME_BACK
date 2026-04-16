package com.example.playgame.service.impl;

import com.example.playgame.dto.JwtResponseDto;
import com.example.playgame.dto.LoginDto;
import com.example.playgame.dto.RegisterDto;
import com.example.playgame.dto.VerifyEmailCodeDto;
import com.example.playgame.entity.Account;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Credential;
import com.example.playgame.entity.EmailVerificationToken;
import com.example.playgame.entity.Role;
import com.example.playgame.entity.enums.Roles;
import com.example.playgame.exception.EmailNotVerifiedException;
import com.example.playgame.exception.InvalidVerificationTokenException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.CredentialRepository;
import com.example.playgame.repository.EmailVerificationTokenRepository;
import com.example.playgame.repository.RoleRepository;
import com.example.playgame.security.CustomUserDetails;
import com.example.playgame.security.CustomUserServiceImpl;
import com.example.playgame.security.jwt.JwtService;
import com.example.playgame.exception.AuthenticationFailedException;
import com.example.playgame.service.AuthenticationService;
import com.example.playgame.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserServiceImpl customUserService;
    private final BucketRepository bucketRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    @Value("${app.email-verification.token-ttl-minutes:30}")
    private long tokenTtlMinutes;


    @Override
    @Transactional
    public JwtResponseDto login(LoginDto loginDto) {
        ensureEmailVerified(loginDto.getLogin());
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
    public void register(RegisterDto registerDto) {
        validateRegistration(registerDto);

        Account newAccount = new Account();
        newAccount.setUsername(registerDto.getUsername());
        newAccount.setEmail(registerDto.getEmail());
        newAccount.setEmailVerified(false);

        Credential credential = new Credential();
        credential.setLogin(registerDto.getLogin());
        credential.setPassword(passwordEncoder.encode(registerDto.getPassword()));


        Role role = roleRepository.findByName(Roles.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER not found in DB"));
        credential.setRoles(Collections.singletonList(role));

        accountRepository.save(newAccount);
        credential.setAccount(newAccount);
        credentialRepository.save(credential);

        Bucket wishlist = Bucket.builder()
                .account(newAccount)
                .dateAdded(new Date())
                .games(new ArrayList<>())
                .build();

        bucketRepository.save(wishlist);

        createAndSendVerificationToken(newAccount);
    }

    @Override
    @Transactional
    public JwtResponseDto verifyEmailCode(VerifyEmailCodeDto verifyEmailCodeDto) {
        Credential credential = credentialRepository.findByLogin(verifyEmailCodeDto.getLogin())
                .orElseThrow(() -> new AuthenticationFailedException("Такого аккаунта не найдено"));
        Account account = credential.getAccount();
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findFirstByAccountAndUsedAtIsNullOrderByCreatedAtDesc(account)
                .orElseThrow(() -> new InvalidVerificationTokenException("Код подтверждения не найден"));

        if (!verificationToken.getToken().equals(verifyEmailCodeDto.getCode())) {
            throw new InvalidVerificationTokenException("Неверный код подтверждения");
        }

        if (verificationToken.getUsedAt() != null) {
            throw new InvalidVerificationTokenException("Токен подтверждения уже использован");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (verificationToken.getExpiresAt().before(now)) {
            throw new InvalidVerificationTokenException("Срок действия токена истек");
        }

        account.setEmailVerified(true);
        account.setEmailVerifiedAt(now);
        verificationToken.setUsedAt(now);
        accountRepository.save(account);
        emailVerificationTokenRepository.save(verificationToken);

        UserDetails user = customUserService.loadUserByUsername(credential.getLogin());
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
    public void resendVerificationEmail(String login) {
        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new AuthenticationFailedException("Такого аккаунта не найдено"));

        Account account = credential.getAccount();
        if (account.isEmailVerified()) {
            throw new IllegalArgumentException("Email уже подтвержден");
        }

        emailVerificationTokenRepository.deleteByAccount(account);
        createAndSendVerificationToken(account);
    }

    private void validateCredentials(LoginDto loginDto){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword())
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: " + loginDto.getLogin(), e);
            throw new AuthenticationFailedException("Неверный логин или пароль");
        }

        log.info("Authentication successful");
    }

    private void validateRegistration(RegisterDto registerDto) {
        if (accountRepository.existsByUsername(registerDto.getUsername())) {
            throw new IllegalArgumentException("Аккаунт с таким именем уже существует");
        }
        if (credentialRepository.existsByLogin(registerDto.getLogin())) {
            throw new IllegalArgumentException("Логин уже используется");
        }
        boolean passwordAlreadyUsed = credentialRepository.findAll().stream()
                .anyMatch(c -> passwordEncoder.matches(registerDto.getPassword(), c.getPassword()));
        if (passwordAlreadyUsed) {
            throw new IllegalArgumentException("Пароль уже используется другим пользователем");
        }
    }

    private void ensureEmailVerified(String login) {
        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new AuthenticationFailedException("Такого аккаунта не найдено"));

        if (!credential.getAccount().isEmailVerified()) {
            throw new EmailNotVerifiedException("Подтвердите email перед входом");
        }
    }

    private void createAndSendVerificationToken(Account account) {
        String rawToken = generateSixDigitCode();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + tokenTtlMinutes * 60_000);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(rawToken)
                .account(account)
                .expiresAt(expiresAt)
                .build();

        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(account.getEmail(), account.getUsername(), rawToken);
    }

    private String generateSixDigitCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }
}