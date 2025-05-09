package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Game;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final GameRepository gameRepository;
    private final FavouriteGenresServiceImpl favouriteGenresService;
    private final GenreRepository genreRepository;
    private final GameDtoMapper gameDtoMapper;

    @Override
    public List<GameShortcutResponseDto> getRecommendationsForUser (Long accountId, int limit, int page) {
        int countGenres = genreRepository.countFavouriteGenresByAccountId(accountId);

        if (countGenres < 3) {
            throw new IllegalArgumentException("You must have more than 3 favourite genres to update.");
        }

        favouriteGenresService.updateFavouriteGenres(accountId);

        return getRecommendedGames(accountId, limit, page);
    }

    List<GameShortcutResponseDto> getRecommendedGames(Long accountId, int limit, int page) {
        List<Long> favouriteGenreIds = genreRepository.findFavouriteGenresByAccountId(accountId);

        if (favouriteGenreIds.isEmpty()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.desc("rating")));

        Page<Game> gamePage = gameRepository.findTopRatedGames(pageable);

        return gamePage.stream()
                .filter(game -> game.getGenres().stream()
                        .anyMatch(genre -> favouriteGenreIds.contains(genre.getId())))
                .map(gameDtoMapper::gameToGameShortcutResponseDto)
                .collect(Collectors.toList());
    }
}