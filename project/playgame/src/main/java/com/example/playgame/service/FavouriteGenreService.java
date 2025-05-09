package com.example.playgame.service;

import java.util.List;

public interface FavouriteGenreService {
    void updateFavouriteGenres(Long accountId);

    void addToFavouritesForNewAccount(Long accountId, List<Long> genreIds);
}
