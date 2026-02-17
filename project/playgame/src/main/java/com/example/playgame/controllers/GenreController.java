package com.example.playgame.controllers;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;
import com.example.playgame.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public GenreToGetResponseDto getGenreById(@PathVariable Long id) {
        return genreService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGenre(@Valid @RequestBody GenreRequestDto genreDto) {
        genreService.save(genreDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<GenreToGetResponseDto> getAllGenres() {
        return genreService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable Long id) {
        genreService.deleteById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGenre(@PathVariable Long id, @Valid @RequestBody GenreUpdateDto updatedGenreDto) {
        updatedGenreDto.setId(id);
        genreService.update(updatedGenreDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{genreId}/games/sorted-by-rating")
    public List<GameShortcutResponseDto> getGamesByGenreSortedByRating(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return genreService.findGamesByGenreSortedByRating(genreId, limit, page);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/games/sorted-by-rating")
    public List<GameShortcutResponseDto> getGamesByGenresSortedByRating(
            @RequestParam List<Long> genreIds,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return genreService.findGamesByGenresSortedByRating(genreIds, limit, page);
    }
}
