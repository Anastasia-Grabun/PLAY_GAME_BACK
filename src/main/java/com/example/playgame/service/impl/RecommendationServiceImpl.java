package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.service.RecommendationService;
import com.example.playgame.util.ImageUrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final GameRepository gameRepository;
    private final FavouriteGenresServiceImpl favouriteGenresService;
    private final GenreRepository genreRepository;
    private final GameDtoMapper gameDtoMapper;
    private final ImageUrlResolver imageUrlResolver;

    private static final int MIN_FAVORITE_GENRES = 3;
    private static final int DIVERSITY_FETCH_MULTIPLIER = 2;

    @Override
    public List<GameShortcutResponseDto> getRecommendations(Long accountId, int limit, int page) {

        int countGenres = genreRepository.countFavouriteGenresByAccountId(accountId);
        if (countGenres < MIN_FAVORITE_GENRES) {
            throw new IllegalArgumentException(
                    "You must have at least " + MIN_FAVORITE_GENRES + " favourite genres to get recommendations."
            );
        }

        favouriteGenresService.updateFavouriteGenres(accountId);

        return getRecommendedGames(accountId, limit, page);
    }

    List<GameShortcutResponseDto> getRecommendedGames(Long accountId, int limit, int page) {
        List<Long> ownedGameIds = getExcludedGameIds();
        List<Long> favouriteGenreIds = genreRepository.findFavouriteGenresByAccountId(accountId);

        if (favouriteGenreIds.isEmpty()) {
            return getFallbackRecommendations(ownedGameIds, page, limit);
        }

        int fetchLimit = limit * DIVERSITY_FETCH_MULTIPLIER;
        Pageable pageable = PageRequest.of(page, fetchLimit);

        Page<Game> gamePage = gameRepository.findRecommendedGamesByGenresPaged(
                favouriteGenreIds,
                ownedGameIds,
                pageable
        );

        List<Game> games = gamePage.getContent();
        if (games.isEmpty()) {
            return getFallbackRecommendations(ownedGameIds, page, limit);
        }

        List<Game> diverseGames = ensureGenreDiversity(games, favouriteGenreIds, limit);
        List<GameShortcutResponseDto> dtos = diverseGames.stream()
                .map(gameDtoMapper::gameToGameShortcutResponseDto)
                .collect(Collectors.toList());
        dtos.forEach(dto -> dto.setCoverImageUrl(imageUrlResolver.resolve(dto.getCoverImageUrl())));
        return dtos;
    }

    private List<Long> getExcludedGameIds() {
        return Collections.singletonList(-1L);
    }

    private List<GameShortcutResponseDto> getFallbackRecommendations(List<Long> excludedGameIds, int page, int limit) {
        Page<Game> fallback = gameRepository.findTopRatedGamesExcluding(
                excludedGameIds,
                PageRequest.of(page, limit)
        );
        List<GameShortcutResponseDto> dtos = fallback.getContent().stream()
                .map(gameDtoMapper::gameToGameShortcutResponseDto)
                .collect(Collectors.toList());
        dtos.forEach(dto -> dto.setCoverImageUrl(imageUrlResolver.resolve(dto.getCoverImageUrl())));
        return dtos;
    }

    private List<Game> ensureGenreDiversity(List<Game> games, List<Long> favouriteGenreIds, int limit) {
        if (games.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        if (games.size() <= limit) {
            return games;
        }

        Map<Long, List<Game>> gamesByGenre = new HashMap<>();
        List<Game> multiGenreGames = new ArrayList<>();

        for (Game game : games) {
            if (game.getGenres() == null || game.getGenres().isEmpty()) {
                continue;
            }

            long matchCount = game.getGenres().stream()
                    .map(Genre::getId)
                    .filter(favouriteGenreIds::contains)
                    .count();

            if (matchCount > 1) {
                multiGenreGames.add(game);
            } else if (matchCount == 1) {
                Long matchingGenreId = game.getGenres().stream()
                        .map(Genre::getId)
                        .filter(favouriteGenreIds::contains)
                        .findFirst()
                        .orElse(null);

                if (matchingGenreId != null) {
                    gamesByGenre.computeIfAbsent(matchingGenreId, k -> new ArrayList<>()).add(game);
                }
            }
        }

        List<Game> result = new ArrayList<>();
        Set<Long> usedGenres = new HashSet<>();

        for (Game game : multiGenreGames) {
            if (result.size() >= limit) break;
            result.add(game);
            if (game.getGenres() != null) {
                game.getGenres().forEach(genre -> usedGenres.add(genre.getId()));
            }
        }

        List<Long> genreOrder = new ArrayList<>(favouriteGenreIds);
        Collections.shuffle(genreOrder);

        for (Long genreId : genreOrder) {
            if (result.size() >= limit) break;

            List<Game> genreGames = gamesByGenre.getOrDefault(genreId, Collections.emptyList());
            for (Game game : genreGames) {
                if (result.size() >= limit) break;
                if (!result.contains(game)) {
                    result.add(game);
                    usedGenres.add(genreId);
                }
            }
        }

        if (result.size() < limit) {
            for (Game game : games) {
                if (result.size() >= limit) break;
                if (!result.contains(game)) {
                    result.add(game);
                }
            }
        }

        return result.stream().limit(limit).collect(Collectors.toList());
    }
}
