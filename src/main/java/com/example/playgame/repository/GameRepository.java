package com.example.playgame.repository;

import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findByDeveloperId(Long developerId, Pageable pageable);

    Optional<Game> findByName(String name);

    Page<Game> findAllByOrderByRatingDesc(Pageable pageable);

    Page<Game> findAllByOrderByRatingAsc(Pageable pageable);

    @Query("SELECT g FROM Game g ORDER BY g.rating DESC")
    Page<Game> findTopRatedGames(Pageable pageable);

    Page<Game> findAllByOrderByPriceAsc(Pageable pageable);

    Page<Game> findAllByOrderByPriceDesc(Pageable pageable);

    @Query("SELECT g FROM Game g JOIN g.genres genre WHERE genre.id = :genreId")
    Page<Game> findByGenre(Long genreId, Pageable pageable);

    Page<Game> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Game> findByGenresContaining(Genre genre, Pageable pageable);

    Page<Game> findByGenresIn(List<Genre> genres, Pageable pageable);

    @Modifying
    @Query(value = """
            INSERT INTO games_ratings (account_id, game_id, rating) 
            VALUES (:accountId, :gameId, :rating) 
            ON CONFLICT (account_id, game_id) DO NOTHING
            """, nativeQuery = true)
    void addRatingIfNotExists(@Param("accountId") Long accountId,
                              @Param("gameId") Long gameId,
                              @Param("rating") BigDecimal rating);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM games_ratings WHERE account_id = :accountId AND game_id = :gameId)", nativeQuery = true)
    boolean existsRatingByAccountAndGame(@Param("accountId") Long accountId, @Param("gameId") Long gameId);

    @Query(value = "SELECT rating FROM games_ratings WHERE account_id = :accountId AND game_id = :gameId", nativeQuery = true)
    Optional<BigDecimal> getRatingByAccountAndGame(@Param("accountId") Long accountId, @Param("gameId") Long gameId);
}

