package com.example.playgame.repository;

import com.example.playgame.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByCredential_Login(String login);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.id IN (SELECT DISTINCT g.developer.id FROM Game g WHERE g.developer IS NOT NULL) ORDER BY a.username")
    List<Account> findDevelopersWithGames();
}