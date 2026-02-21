package com.example.playgame.controllers;

import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.service.AccountService;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.FavouriteGenreService;
import com.example.playgame.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Accounts", description = "Аккаунты пользователей: профиль, баланс, рекомендации, избранные жанры")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private final AccountService accountService;
    private final AuthService authService;
    private final RecommendationService recommendationService;
    private final FavouriteGenreService favouriteGenreService;

    //тут просто юзер
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public AccountResponseDto getCurrentAccount(@AuthenticationPrincipal UserDetails userDetails) {
        return accountService.getByLogin(userDetails.getUsername());
    }

    //а зачем
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountResponseDto getAccountById(@PathVariable("id") Long id) {
        return accountService.getById(id);
    }

    //юзер, который и является владельцем)
    @PutMapping()
    @PreAuthorize("hasRole('USER')")
    public AccountResponseDto updateAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AccountUpdateDto accountDto) {
        return accountService.updateByLogin(userDetails.getUsername(), accountDto);
    }

    //
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public void deleteCurrentAccount(@AuthenticationPrincipal UserDetails userDetails) {
        accountService.deleteByLogin(userDetails.getUsername());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccountById(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @GetMapping("/me/balance")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public BigDecimal getCurrentBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return accountService.getBalanceByLogin(userDetails.getUsername());
    }

    //удалить, зачем нужно
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public BigDecimal getBalanceByAccountId(@PathVariable("id") Long accountId) {
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
    @GetMapping("/recommendations")
    public List<GameShortcutResponseDto> getRecommendationsForUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return recommendationService.getRecommendations(accountId, limit, page);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favourites")
    public void addToFavouritesFromToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<Long> genreIds) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        favouriteGenreService.addToFavourites(accountId, genreIds);
    }
}


