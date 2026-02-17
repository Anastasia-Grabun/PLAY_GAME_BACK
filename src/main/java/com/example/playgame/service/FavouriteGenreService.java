package com.example.playgame.service;

import java.util.List;

public interface FavouriteGenreService {
    void updateFavouriteGenres(Long accountId);

    void addToFavouritesUsingToken(String authHeader, List<Long> genreIds);
}
