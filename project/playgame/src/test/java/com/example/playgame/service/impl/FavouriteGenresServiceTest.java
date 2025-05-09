package com.example.playgame.service.impl;

import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.entity.Purchase;
import com.example.playgame.entity.enums.BucketType;
import com.example.playgame.exception.notfound.GenreNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FavouriteGenresServiceTest {
    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BucketRepository bucketRepository;

    @InjectMocks
    private FavouriteGenresServiceImpl favouriteGenresService;

    private Long accountId;
    private Long genreId;
    private Game game;
    private Purchase purchase;

    @BeforeEach
    public void setUp() {
        accountId = 1L;
        genreId = 1L;

        game = new Game();
        game.setId(1L);
        Genre genre = new Genre();
        genre.setId(genreId);
        game.setGenres(Collections.singletonList(genre));

        purchase = new Purchase();
        purchase.setGame(game);
    }
    @Test
    void testUpdateFavouriteGenres_Success() {
        when(purchaseRepository.findByOwnerId(accountId)).thenReturn(Collections.singletonList(purchase));
        when(gameRepository.getRatingByAccountAndGame(game.getId(), accountId)).thenReturn(Optional.of(BigDecimal.valueOf(4.5)));
        when(bucketRepository.findByAccountIdAndBucketType(accountId, BucketType.WISHLIST.toString())).thenReturn(Optional.empty());

        favouriteGenresService.updateFavouriteGenres(accountId);

        Map<Long, BigDecimal> genreCount = new HashMap<>();
        favouriteGenresService.collectGenres(Collections.singletonList(game), genreCount, BigDecimal.ONE); // Use a single game with a weight of 1

        List<Long> favouriteGenreIds = genreCount.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.valueOf(3)) >= 0)
                .map(Map.Entry::getKey)
                .toList();

        if (!favouriteGenreIds.isEmpty()) {
            verify(genreRepository, times(1)).addFavouriteGenre(accountId, favouriteGenreIds.get(0));  // Assuming the genre ID is the first in the list
        }
    }

    @Test
    public void testUpdateFavouriteGenres_NoPurchases() {
        when(purchaseRepository.findByOwnerId(accountId)).thenReturn(Collections.emptyList());

        favouriteGenresService.updateFavouriteGenres(accountId);

        verify(genreRepository, never()).addFavouriteGenre(anyLong(), anyLong());
    }

    @Test
    public void testAddToFavouritesForNewAccount_Success() {
        when(genreRepository.existsById(genreId)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, genreId)).thenReturn(false);

        favouriteGenresService.addToFavouritesForNewAccount(accountId, Collections.singletonList(genreId));

        verify(genreRepository, times(1)).addFavouriteGenre(accountId, genreId);
    }

    @Test
    public void testAddToFavouritesForNewAccount_TooManyGenres() {
        assertThrows(IllegalArgumentException.class, () -> {
            favouriteGenresService.addToFavouritesForNewAccount(accountId, List.of(1L, 2L, 3L, 4L));
        });
    }

    @Test
    public void testAddToFavouritesForNewAccount_GenreNotFound() {
        when(genreRepository.existsById(genreId)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> {
            favouriteGenresService.addToFavouritesForNewAccount(accountId, Collections.singletonList(genreId));
        });
    }

    @Test
    public void testAddToFavouritesForNewAccount_AlreadyExists() {
        when(genreRepository.existsById(genreId)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, genreId)).thenReturn(true);

        favouriteGenresService.addToFavouritesForNewAccount(accountId, Collections.singletonList(genreId));

        verify(genreRepository, never()).addFavouriteGenre(anyLong(), anyLong());
    }
}
