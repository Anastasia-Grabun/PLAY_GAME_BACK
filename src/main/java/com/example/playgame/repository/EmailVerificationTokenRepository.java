package com.example.playgame.repository;

import com.example.playgame.entity.Account;
import com.example.playgame.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findFirstByAccountAndUsedAtIsNullOrderByCreatedAtDesc(Account account);

    void deleteByAccount(Account account);
}
