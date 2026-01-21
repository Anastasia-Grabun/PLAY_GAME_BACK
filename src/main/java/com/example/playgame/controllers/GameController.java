package com.example.playgame.controllers;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
public class GameController {
    
    private final GameService gameService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public GameResponseDto getGameById(@PathVariable Long id) {
        return gameService.getById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DEVELOPER')")
    public void createGame(@RequestBody GameRequestDto gameDto) {
        gameService.save(gameDto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public List<GameShortcutResponseDto> getAllGames(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return gameService.getAll(page, limit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteById(id);
    }

    @PutMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public void updateGame(@RequestBody GameUpdateDto updatedGameDto) {
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

    @GetMapping("/top-rated")
    @PreAuthorize("hasRole('USER')")
    public List<GameShortcutResponseDto> sortGamesByRating(
            @RequestParam(defaultValue = "false") Boolean ascending,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return gameService.sortGamesByRating(ascending, page, limit);
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
            @RequestParam(defaultValue = "10") Integer limit){
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

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/bucket/{bucketId}/game/{gameId}")
    public void addGameToBucket(@PathVariable Long bucketId, @PathVariable Long gameId) {
        gameService.addGameToBucket(bucketId, gameId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/bucket/{bucketId}/game/{gameId}")
    public void removeGameFromBucket(@PathVariable Long bucketId, @PathVariable Long gameId) {
        gameService.removeGameFromBucket(bucketId, gameId);
    }

    @PostMapping("/add-rating")
    @PreAuthorize("hasRole('USER')")
    public void addRating(
            @RequestParam Long gameId,
            @RequestParam Long accountId,
            @RequestParam BigDecimal rating) {
        gameService.addRating(gameId, accountId, rating);
    }

}
