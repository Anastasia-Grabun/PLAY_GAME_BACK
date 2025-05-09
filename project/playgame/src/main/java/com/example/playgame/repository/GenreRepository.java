package com.example.playgame.repository;

import com.example.playgame.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query(value = "SELECT genre_id FROM favourite_genres WHERE account_id = :accountId", nativeQuery = true)
    List<Long> findFavouriteGenresByAccountId(@Param("accountId") Long accountId);

    @Query(value = "SELECT COUNT(genre_id) FROM favourite_genres WHERE account_id = :accountId", nativeQuery = true)
    int countFavouriteGenresByAccountId(@Param("accountId") Long accountId);

    @Query(value = "SELECT COUNT(*) > 0 FROM favourite_genres WHERE account_id = :accountId AND genre_id = :genreId", nativeQuery = true)
    boolean existsByAccountIdAndGenreId(@Param("accountId") Long accountId, @Param("genreId") Long genreId);

    @Modifying
    @Query(value = "INSERT INTO favourite_genres (account_id, genre_id) VALUES (:accountId, :genreId)", nativeQuery = true)
    void addFavouriteGenre(@Param("accountId") Long accountId, @Param("genreId") Long genreId);
}
