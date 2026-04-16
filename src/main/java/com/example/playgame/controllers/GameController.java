package com.example.playgame.controllers;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Games", description = "Управление играми: просмотр, создание, обновление, рейтинги")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
@Validated
public class GameController {

    private final GameService gameService;
    private final AuthService authService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public GameResponseDto getGameById(@PathVariable Long id) {
        return gameService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGame(@Valid @RequestBody GameRequestDto gameDto) {
        gameService.save(gameDto);
    }

    //девелопер ток этой игры
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteById(id);
    }

    //девелопер ток этой игры
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEVELOPER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGame(@PathVariable Long id, @Valid @RequestBody GameUpdateDto updatedGameDto) {
        /*updatedGameDto.setId(id);*/
        gameService.update(updatedGameDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/developer/{developerId}")
    public List<GameShortcutResponseDto> getGamesByDeveloper(
            @PathVariable Long developerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.findGamesByDeveloper(developerId, page, limit);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('USER')")
    public GameShortcutResponseDto getGameByName(@PathVariable String name) {
        return gameService.findGameByName(name);
    }

    @GetMapping("/developers")
    @PreAuthorize("hasRole('USER')")
    public List<AccountWithIdAndUsernameDto> getDevelopers() {
        return gameService.getDevelopers();
    }

    @GetMapping("/top-rated")
    @PreAuthorize("hasRole('USER')")
    public List<GameShortcutResponseDto> sortGamesByRating(
            @RequestParam(defaultValue = "false") Boolean ascending,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.sortGamesByRating(ascending, page, limit);
    }

    @GetMapping("/new-releases")
    @PreAuthorize("hasRole('USER')")
    public List<GameShortcutResponseDto> getNewReleases(
            @RequestParam(defaultValue = "6") Integer limit) {
        return gameService.getNewReleases(limit);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/sorted-by-price")
    public List<GameShortcutResponseDto> sortGamesByPrice(
            @RequestParam(defaultValue = "true") Boolean ascending,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.sortGamesByPrice(ascending, page, limit);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/genre/{genreId}")
    public List<GameShortcutResponseDto> getGamesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.findGamesByGenre(genreId, page, limit);
    }

    @GetMapping("/price-range")
    @PreAuthorize("hasRole('USER')")
    public List<GameShortcutResponseDto> getGamesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.findGamesInPriceRange(minPrice, maxPrice, page, limit);
    }

    @GetMapping("/{gameId}/me/has-rated")
    @PreAuthorize("hasRole('USER')")
    public boolean hasRated(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return gameService.hasRated(gameId, accountId);
    }

    @PostMapping("/{gameId}/rating")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long gameId,
            @RequestParam @DecimalMin("0") @DecimalMax("5") BigDecimal rating) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        gameService.addRating(gameId, accountId, rating);
    }
}
