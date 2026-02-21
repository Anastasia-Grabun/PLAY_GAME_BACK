package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.repository.PurchaseRepository;
import com.example.playgame.service.RecommendationService;
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
    private final PurchaseRepository purchaseRepository;
    private final GameDtoMapper gameDtoMapper;

    // Minimum number of favorite genres required for recommendations
    private static final int MIN_FAVORITE_GENRES = 3;
    // Number of games to fetch before applying diversity filter (to ensure variety)
    private static final int DIVERSITY_FETCH_MULTIPLIER = 2;

    @Override
    public List<GameShortcutResponseDto> getRecommendations(Long accountId, int limit, int page) {

        int countGenres = genreRepository.countFavouriteGenresByAccountId(accountId);
        if (countGenres < MIN_FAVORITE_GENRES) {
            throw new IllegalArgumentException(
                    "You must have at least " + MIN_FAVORITE_GENRES + " favourite genres to get recommendations."
            );
        }

        // Update favorite genres based on recent purchases and wishlist
        favouriteGenresService.updateFavouriteGenres(accountId);

        return getRecommendedGames(accountId, limit, page);
    }

    /**
     * Get recommended games based on favorite genres with improved algorithm:
     * 1. Uses optimized database query filtering by genres at DB level
     * 2. Excludes games already owned by the user
     * 3. Prioritizes games matching multiple favorite genres
     * 4. Ensures diversity across genres
     * 5. Orders by genre match count and rating
     */
    List<GameShortcutResponseDto> getRecommendedGames(Long accountId, int limit, int page) {
        List<Long> ownedGameIds = getExcludedGameIds(accountId);
        List<Long> favouriteGenreIds = genreRepository.findFavouriteGenresByAccountId(accountId);

        // No favourite genres: use fallback (top-rated, excluding owned) instead of empty list
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
        return diverseGames.stream()
                .map(gameDtoMapper::gameToGameShortcutResponseDto)
                .collect(Collectors.toList());
    }

    /** List of game IDs to exclude from recommendations (owned). Never empty for SQL safety. */
    private List<Long> getExcludedGameIds(Long accountId) {
        List<Long> owned = purchaseRepository.findGameIdsByOwnerId(accountId);
        return (owned == null || owned.isEmpty())
                ? Collections.singletonList(-1L)
                : owned;
    }

    /** Fallback when no genre-based recommendations: top-rated games excluding owned. */
    private List<GameShortcutResponseDto> getFallbackRecommendations(List<Long> excludedGameIds, int page, int limit) {
        Page<Game> fallback = gameRepository.findTopRatedGamesExcluding(
                excludedGameIds,
                PageRequest.of(page, limit)
        );
        return fallback.getContent().stream()
                .map(gameDtoMapper::gameToGameShortcutResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Ensures recommendations include games from multiple genres for better diversity.
     * Prioritizes games matching multiple favorite genres while ensuring genre variety.
     */
    private List<Game> ensureGenreDiversity(List<Game> games, List<Long> favouriteGenreIds, int limit) {
        if (games.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        // If we have fewer games than requested, return all
        if (games.size() <= limit) {
            return games;
        }

        // Group games by their primary matching genre
        Map<Long, List<Game>> gamesByGenre = new HashMap<>();
        List<Game> multiGenreGames = new ArrayList<>();

        for (Game game : games) {
            if (game.getGenres() == null || game.getGenres().isEmpty()) {
                continue;
            }

            // Count how many favorite genres this game matches
            long matchCount = game.getGenres().stream()
                    .map(Genre::getId)
                    .filter(favouriteGenreIds::contains)
                    .count();

            if (matchCount > 1) {
                // Prioritize games matching multiple genres
                multiGenreGames.add(game);
            } else if (matchCount == 1) {
                // Group single-genre matches by their matching genre
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

        // Build diverse result list
        List<Game> result = new ArrayList<>();
        Set<Long> usedGenres = new HashSet<>();

        // First, add multi-genre games (highest priority)
        for (Game game : multiGenreGames) {
            if (result.size() >= limit) break;
            result.add(game);
            // Track genres used
            if (game.getGenres() != null) {
                game.getGenres().forEach(genre -> usedGenres.add(genre.getId()));
            }
        }

        // Then, add games from different genres to ensure diversity
        // Rotate through genres to ensure fair representation
        List<Long> genreOrder = new ArrayList<>(favouriteGenreIds);
        Collections.shuffle(genreOrder); // Randomize order for variety

        for (Long genreId : genreOrder) {
            if (result.size() >= limit) break;
            
            List<Game> genreGames = gamesByGenre.getOrDefault(genreId, Collections.emptyList());
            for (Game game : genreGames) {
                if (result.size() >= limit) break;
                // Avoid duplicates
                if (!result.contains(game)) {
                    result.add(game);
                    usedGenres.add(genreId);
                }
            }
        }

        // Fill remaining slots with any remaining games if needed
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