package com.example.playgame.service;

public interface AuthService {
    Long extractAccountId(String authHeader);

    /** Resolve account id by login (e.g. from UserDetails.getUsername()). */
    Long getAccountIdByLogin(String login);
}
