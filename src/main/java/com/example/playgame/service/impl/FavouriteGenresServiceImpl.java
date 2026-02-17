package com.example.playgame.service.impl;

import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.entity.Purchase;
import com.example.playgame.entity.enums.BucketType;
import com.example.playgame.exception.notfound.GenreNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.repository.PurchaseRepository;
import com.example.playgame.service.FavouriteGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavouriteGenresServiceImpl implements FavouriteGenreService {
    private final PurchaseRepository purchaseRepository;
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final BucketRepository bucketRepository;
    private final AuthServiceImpl authService;

    private static final BigDecimal RATING_THRESHOLD = BigDecimal.valueOf(3.5);
    private static final int MIN_GENRE_OCCURRENCES = 3;
    private static final BigDecimal WISHLIST_GENRE_WEIGHT = BigDecimal.valueOf(0.5);
    private static final BigDecimal PURCHASE_GENRE_WEIGHT = BigDecimal.valueOf(1.0);

    @Override
    public void updateFavouriteGenres(Long accountId) {
        List<Purchase> purchases = purchaseRepository.findByOwnerId(accountId);

        List<Game> filteredPurchases = purchases.stream()
                .map(Purchase::getGame)
                .filter(game -> isGameRatedHighEnough(accountId, game))
                .collect(Collectors.toList());

        Map<Long, BigDecimal> genreCount = new HashMap<>();
        collectGenres(filteredPurchases, genreCount, PURCHASE_GENRE_WEIGHT);

        List<Game> wishlistGames = getWishlistGames(accountId);
        collectGenres(wishlistGames, genreCount, WISHLIST_GENRE_WEIGHT);

        List<Long> favouriteGenreIds = genreCount.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.valueOf(MIN_GENRE_OCCURRENCES)) >= 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        favouriteGenreIds.forEach(genreId -> addToFavourites(accountId, genreId));
    }


    @Override
    @Transactional
    public void addToFavouritesUsingToken(String authHeader, List<Long> genreIds) {
        Long accountId = authService.extractAccountId(authHeader);
        validateAndAddGenres(accountId, genreIds);
    }

    private void validateAndAddGenres(Long accountId, List<Long> genreIds) {
        if (genreIds.size() > 3) {
            throw new IllegalArgumentException("You can't add more than 3 favourite genres");
        }

        for (Long genreId : genreIds) {
            if (!genreRepository.existsById(genreId)) {
                throw new GenreNotFoundException(genreId);
            }

            if (!genreRepository.existsByAccountIdAndGenreId(accountId, genreId)) {
                genreRepository.addFavouriteGenre(accountId, genreId);
            }
        }
    }

    private boolean isGameRatedHighEnough(Long accountId, Game game) {
        return gameRepository.getRatingByAccountAndGame(game.getId(), accountId)
                .map(gameRating -> gameRating.compareTo(RATING_THRESHOLD) >= 0)
                .orElse(true);
    }

    void collectGenres(List<Game> games, Map<Long, BigDecimal> genreCount, BigDecimal weight) {
        for (Game game : games) {
            if (game.getGenres() != null) {
                for (Genre genre : game.getGenres()) {
                    genreCount.put(genre.getId(), genreCount.getOrDefault(genre.getId(), BigDecimal.ZERO).add(weight));
                }
            }
        }
    }


    private List<Game> getWishlistGames(Long accountId) {
        Optional<Bucket> wishlist = bucketRepository
                .findByAccountIdAndBucketType(accountId, BucketType.WISHLIST.toString());

        return wishlist.map(Bucket::getGames)
                .orElseGet(Collections::emptyList);
    }

    private void addToFavourites(Long accountId, Long genreId) {
        if (!genreRepository.existsByAccountIdAndGenreId(accountId, genreId)) {
            genreRepository.addFavouriteGenre(accountId, genreId);
        }
    }
}


