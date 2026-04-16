package com.example.playgame.controllers;

import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.service.AccountService;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.FavouriteGenreService;
import com.example.playgame.security.CustomUserDetails;
import com.example.playgame.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Accounts", description = "Аккаунты пользователей: профиль, рекомендации, избранные жанры")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private final AccountService accountService;
    private final AuthService authService;
    private final RecommendationService recommendationService;
    private final FavouriteGenreService favouriteGenreService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public AccountResponseDto getCurrentAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return accountService.getByLogin(customUserDetails.getUsername());
    }

    @GetMapping("/me/roles")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public List<String> getMyRoles(@AuthenticationPrincipal CustomUserDetails user) {
        return user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
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
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody AccountUpdateDto accountDto) {
        return accountService.updateByLogin(customUserDetails.getUsername(), accountDto);
    }

    //
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DEVELOPER')")
    public void deleteCurrentAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        accountService.deleteByLogin(customUserDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccountById(@PathVariable("id") Long id) {
        accountService.deleteById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/recommendations")
    public List<GameShortcutResponseDto> getRecommendationsForUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page) {
        Long accountId = authService.getAccountIdByLogin(customUserDetails.getUsername());
        return recommendationService.getRecommendations(accountId, limit, page);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/favourites")
    public void addToFavouritesFromToken(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<Long> genreIds) {
        Long accountId = authService.getAccountIdByLogin(customUserDetails.getUsername());
        favouriteGenreService.addToFavourites(accountId, genreIds);
    }
}
