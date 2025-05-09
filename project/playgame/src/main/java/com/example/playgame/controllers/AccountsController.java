package com.example.playgame.controllers;

import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.service.AccountService;
import com.example.playgame.service.FavouriteGenreService;
import com.example.playgame.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private final AccountService accountService;
    private final RecommendationService recommendationService;
    private final FavouriteGenreService favouriteGenreService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public AccountResponseDto getAccountById(@PathVariable("id") Long id) {
        return accountService.getById(id);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public AccountResponseDto getAccountByUsername(@PathVariable("username") String username) {
        return accountService.findByUsername(username);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public List<AccountShortcutResponseDto> getAllAccounts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return accountService.getAll(limit, page);
    }

    @PutMapping()
    @PreAuthorize("hasRole('USER')")
    public AccountResponseDto updateAccount(@Valid @RequestBody AccountUpdateDto accountDto) {
        return accountService.update(accountDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BigDecimal getBalance(@PathVariable("id") Long accountId) {
        return accountService.checkBalance(accountId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{accountId}/balance")
    public void updateAccountBalance(
            @PathVariable Long accountId,
            @RequestBody BigDecimal newBalance) {
        accountService.updateBalance(accountId, newBalance);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{accountId}/recommendations")
    public List<GameShortcutResponseDto> getRecommendationsForUser (
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page) {
        return recommendationService.getRecommendationsForUser(accountId, limit, page);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{accountId}/favourites")
    public void addToFavouritesForNewAccount(
            @PathVariable Long accountId,
            @RequestBody List<Long> genreIds) {
        favouriteGenreService.addToFavouritesForNewAccount(accountId, genreIds);
    }
}


