package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private FavouriteGenresServiceImpl favouriteGenresService;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameDtoMapper gameDtoMapper;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Long accountId;
    private Game game;
    private GameShortcutResponseDto gameShortcutResponseDto;

    @BeforeEach
    public void setUp() {
        accountId = 1L;

        game = new Game();
        game.setId(1L);
        game.setName("Test Game");
        game.setGenres(new ArrayList<>());

        gameShortcutResponseDto = new GameShortcutResponseDto();
        gameShortcutResponseDto.setId(1L);
        gameShortcutResponseDto.setName("Test Game");
    }

    @Test
    public void testGetRecommendationsForUser_Success() {
        when(genreRepository.countFavouriteGenresByAccountId(accountId)).thenReturn(3);

        doNothing().when(favouriteGenresService).updateFavouriteGenres(accountId);

        when(genreRepository.findFavouriteGenresByAccountId(accountId)).thenReturn(Collections.singletonList(1L));

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Action");
        game.setGenres(Collections.singletonList(genre));

        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findTopRatedGames(any(Pageable.class))).thenReturn(gamePage);
        when(gameDtoMapper.gameToGameShortcutResponseDto(game)).thenReturn(gameShortcutResponseDto);

        List<GameShortcutResponseDto> result = recommendationService.getRecommendationsForUser (accountId, 10, 0);

        assertEquals(1, result.size());
        assertEquals(gameShortcutResponseDto, result.get(0));
    }

    @Test
    public void testGetRecommendationsForUser_InsufficientGenres() {
        when(genreRepository.countFavouriteGenresByAccountId(accountId)).thenReturn(2);

        assertThrows(IllegalArgumentException.class, () -> recommendationService.getRecommendationsForUser (accountId, 10, 0));
    }

    @Test
    public void testGetRecommendedGames_NoGenres() {
        when(genreRepository.findFavouriteGenresByAccountId(accountId)).thenReturn(Collections.emptyList());

        List<GameShortcutResponseDto> result = recommendationService.getRecommendedGames(accountId, 10, 0);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetRecommendedGames_Success() {
        when(genreRepository.findFavouriteGenresByAccountId(accountId)).thenReturn(Collections.singletonList(1L));

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Action");
        game.getGenres().add(genre);

        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findTopRatedGames(any(Pageable.class))).thenReturn(gamePage);
        when(gameDtoMapper.gameToGameShortcutResponseDto(game)).thenReturn(gameShortcutResponseDto);

        List<GameShortcutResponseDto> result = recommendationService.getRecommendedGames(accountId, 10, 0);

        assertEquals(1, result.size());
        assertEquals(gameShortcutResponseDto, result.get(0));
    }
}
