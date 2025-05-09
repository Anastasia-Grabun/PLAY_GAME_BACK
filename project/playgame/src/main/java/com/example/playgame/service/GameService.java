package com.example.playgame.service;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface GameService {
    GameResponseDto getById(Long id);

    void save(GameRequestDto gameDto);

    List<GameShortcutResponseDto> getAll(int page, int size);

    void deleteById(Long id);

    void update(GameUpdateDto updatedGameDto);

    List<GameShortcutResponseDto> findGamesByDeveloper(Long developerId, int page, int size);

    GameShortcutResponseDto findGameByName(String name);

    List<GameShortcutResponseDto> sortGamesByRating(boolean ascending, int page, int size);

    List<GameShortcutResponseDto> sortGamesByPrice(boolean ascending, int page, int size);

    List<GameShortcutResponseDto> findGamesByGenre(Long genreId, int page, int size);

    List<GameShortcutResponseDto> findGamesInPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size);

    void addGameToBucket(Long bucketId, Long gameId);

    void removeGameFromBucket(Long bucketId, Long gameId);

    void addRating(Long gameId, Long accountId, BigDecimal rating);
}
