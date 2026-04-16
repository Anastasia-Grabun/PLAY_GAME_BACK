package com.example.playgame.service.impl;

import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.exception.notfound.GenreNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private GenreRepository genreRepository;

    @Mock
    private BucketRepository bucketRepository;

    @InjectMocks
    private FavouriteGenresServiceImpl favouriteGenresService;

    private Long accountId;
    private Long genreId;
    private Game gameWithGenre;

    @BeforeEach
    public void setUp() {
        accountId = 1L;
        genreId = 1L;

        gameWithGenre = new Game();
        gameWithGenre.setId(1L);
        Genre genre = new Genre();
        genre.setId(genreId);
        gameWithGenre.setGenres(Collections.singletonList(genre));
    }

    @Test
    void testUpdateFavouriteGenres_FromWishlist_AddsGenre() {
        List<Game> wishlist = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Game g = new Game();
            g.setId((long) (i + 1));
            Genre genre = new Genre();
            genre.setId(genreId);
            g.setGenres(Collections.singletonList(genre));
            wishlist.add(g);
        }
        Bucket bucket = new Bucket();
        bucket.setGames(wishlist);

        when(bucketRepository.findBucketByAccount_Id(accountId)).thenReturn(Optional.of(bucket));
        when(genreRepository.existsByAccountIdAndGenreId(accountId, genreId)).thenReturn(false);

        favouriteGenresService.updateFavouriteGenres(accountId);

        verify(genreRepository, times(1)).addFavouriteGenre(accountId, genreId);
    }

    @Test
    public void testUpdateFavouriteGenres_EmptyWishlist() {
        when(bucketRepository.findBucketByAccount_Id(accountId)).thenReturn(Optional.empty());

        favouriteGenresService.updateFavouriteGenres(accountId);

        verify(genreRepository, never()).addFavouriteGenre(anyLong(), anyLong());
    }

    @Test
    public void testAddToFavouritesForNewAccount_Success() {
        when(genreRepository.existsById(genreId)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, genreId)).thenReturn(false);

        favouriteGenresService.addToFavourites(accountId, List.of(1L, 2L, 3L));

        verify(genreRepository, times(1)).addFavouriteGenre(accountId, 1L);
        verify(genreRepository, times(1)).addFavouriteGenre(accountId, 2L);
        verify(genreRepository, times(1)).addFavouriteGenre(accountId, 3L);
    }

    @Test
    public void testAddToFavouritesForNewAccount_TooManyGenres() {
        assertThrows(IllegalArgumentException.class, () -> {
            favouriteGenresService.addToFavourites(accountId, List.of(1L, 2L, 3L, 4L));
        });
    }

    @Test
    public void testAddToFavouritesForNewAccount_GenreNotFound() {
        when(genreRepository.existsById(genreId)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> {
            favouriteGenresService.addToFavourites(accountId, List.of(1L, 2L, 3L));
        });
    }

    @Test
    public void testAddToFavouritesForNewAccount_AlreadyExists() {
        when(genreRepository.existsById(1L)).thenReturn(true);
        when(genreRepository.existsById(2L)).thenReturn(true);
        when(genreRepository.existsById(3L)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, 1L)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, 2L)).thenReturn(true);
        when(genreRepository.existsByAccountIdAndGenreId(accountId, 3L)).thenReturn(true);

        favouriteGenresService.addToFavourites(accountId, List.of(1L, 2L, 3L));

        verify(genreRepository, never()).addFavouriteGenre(anyLong(), anyLong());
    }
}
