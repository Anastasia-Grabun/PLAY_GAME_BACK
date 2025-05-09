package com.example.playgame.controllers;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;
import com.example.playgame.service.GenreService;
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

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEVELOPER')")
    public void createGenre(@RequestBody GenreRequestDto genreDto) {
        genreService.save(genreDto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public List<GenreToGetResponseDto> getAllGenres() {
        return genreService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGenre(@PathVariable Long id) {
        genreService.deleteById(id);
    }

    @PutMapping("update")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateGenre(@RequestBody GenreUpdateDto updatedGenreDto) {
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

