package com.example.playgame.controllers;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {
    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private GameResponseDto gameResponseDto;
    private GameRequestDto gameRequestDto;
    private GameUpdateDto gameUpdateDto;

    @BeforeEach
    public void setUp() {
        gameResponseDto = new GameResponseDto();
        gameResponseDto.setId(1L);
        gameResponseDto.setName("Test Game");

        gameRequestDto = new GameRequestDto();
        gameRequestDto.setName("Test Game");

        gameUpdateDto = new GameUpdateDto();
        gameUpdateDto.setId(1L);
        gameUpdateDto.setName("Updated Test Game");
    }

    @Test
    public void testGetGameById_Success() {
        when(gameService.getById(1L)).thenReturn(gameResponseDto);

        GameResponseDto result = gameController.getGameById(1L);

        assertEquals(gameResponseDto, result);
    }

    @Test
    public void testCreateGame_Success() {
        gameController.createGame(gameRequestDto);

        verify(gameService, times(1)).save(gameRequestDto);
    }

    @Test
    public void testGetAllGames_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.getAll(0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.getAllGames(0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testDeleteGame_Success() {
        gameController.deleteGame(1L);

        verify(gameService, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateGame_Success() {
        gameController.updateGame(gameUpdateDto);

        verify(gameService, times(1)).update(gameUpdateDto);
    }

    @Test
    public void testGetGamesByDeveloper_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.findGamesByDeveloper(1L, 0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.getGamesByDeveloper(1L, 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testGetGameByName_Success() {
        when(gameService.findGameByName("Test Game")).thenReturn(new GameShortcutResponseDto());

        GameShortcutResponseDto result = gameController.getGameByName("Test Game");

        assertNotNull(result);
    }

    @Test
    public void testSortGamesByRating_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.sortGamesByRating(true, 0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.sortGamesByRating(true, 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testSortGamesByPrice_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.sortGamesByPrice(true, 0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.sortGamesByPrice(true, 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testGetGamesByGenre_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.findGamesByGenre(1L, 0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.getGamesByGenre(1L, 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testGetGamesByPriceRange_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(gameService.findGamesInPriceRange(BigDecimal.valueOf(10), BigDecimal.valueOf(50), 0, 10)).thenReturn(games);

        List<GameShortcutResponseDto> result = gameController.getGamesByPriceRange(BigDecimal.valueOf(10), BigDecimal.valueOf(50), 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testAddGameToBucket_Success() {
        gameController.addGameToBucket(1L, 2L);

        verify(gameService, times(1)).addGameToBucket(1L, 2L);
    }

    @Test
    public void testRemoveGameFromBucket_Success() {
        gameController.removeGameFromBucket(1L, 2L);

        verify(gameService, times(1)).removeGameFromBucket(1L, 2L);
    }

    @Test
    public void testAddRating_Success() {
        gameController.addRating(1L, 2L, BigDecimal.valueOf(4.5));

        verify(gameService, times(1)).addRating(1L, 2L, BigDecimal.valueOf(4.5));
    }
}
