package com.example.playgame.service;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;

import java.util.List;

public interface GenreService {
    GenreToGetResponseDto getById(Long id);

    void save(GenreRequestDto genreDto);

    List<GenreToGetResponseDto> getAll();

    void deleteById(Long id);

    void update(GenreUpdateDto updatedGenreDto);

    List<GameShortcutResponseDto> findGamesByGenreSortedByRating(Long genreId, int limit, int page);

    List<GameShortcutResponseDto> findGamesByGenresSortedByRating(List<Long> genreIds, int limit, int page);
}
