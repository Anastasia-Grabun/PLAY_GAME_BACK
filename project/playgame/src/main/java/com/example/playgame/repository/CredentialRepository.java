package com.example.playgame.repository;

import com.example.playgame.entity.Credential;
import com.example.playgame.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    boolean existsByLogin(String login);
    Optional<Credential> findByLogin(String login);
    void deleteByLogin(String login);

    Page<Credential> findByRoles(Role role, Pageable pageable);
}