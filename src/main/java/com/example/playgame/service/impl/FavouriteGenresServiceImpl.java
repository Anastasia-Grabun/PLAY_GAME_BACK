package com.example.playgame.service.impl;

import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.exception.notfound.GenreNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GenreRepository;
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

@Service
@RequiredArgsConstructor
public class FavouriteGenresServiceImpl implements FavouriteGenreService {
    private final GenreRepository genreRepository;
    private final BucketRepository bucketRepository;

    private static final int MIN_GENRE_OCCURRENCES = 3;
    private static final BigDecimal WISHLIST_GENRE_WEIGHT = BigDecimal.ONE;

    @Override
    @Transactional
    public void addToFavourites(Long accountId, List<Long> genreIds) {
        validateAndAddGenres(accountId, genreIds);
    }

    @Override
    public void updateFavouriteGenres(Long accountId) {
        Map<Long, BigDecimal> genreCount = new HashMap<>();

        List<Game> wishlistGames = getWishlistGames(accountId);
        collectGenres(wishlistGames, genreCount, WISHLIST_GENRE_WEIGHT);

        List<Long> favouriteGenreIds = genreCount.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.valueOf(MIN_GENRE_OCCURRENCES)) >= 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        favouriteGenreIds.forEach(genreId -> addToFavourites(accountId, genreId));
    }

    private void validateAndAddGenres(Long accountId, List<Long> genreIds) {
        if (genreIds.size() != 3) {
            throw new IllegalArgumentException("You can add only 3 favourite genres");
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
        Optional<Bucket> wishlist = bucketRepository.findBucketByAccount_Id(accountId);

        return wishlist.map(Bucket::getGames)
                .orElseGet(Collections::emptyList);
    }

    private void addToFavourites(Long accountId, Long genreId) {
        if (!genreRepository.existsByAccountIdAndGenreId(accountId, genreId)) {
            genreRepository.addFavouriteGenre(accountId, genreId);
        }
    }
}
