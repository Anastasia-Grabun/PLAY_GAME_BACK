package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.DevelopersGamesNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameDtoMapper gameDtoMapper;

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private Game game;

    private GameRequestDto gameRequestDto;
    private GameResponseDto gameResponseDto;
    private GameUpdateDto gameUpdateDto;

    @BeforeEach
    public void setUp() {
        game = new Game();
        game.setId(1L);
        game.setName("Test Game");
        game.setPrice(BigDecimal.valueOf(59.99));
        game.setRating(BigDecimal.valueOf(4.5));

        gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setName("Test Game");
        gameRequestDto.setPrice(BigDecimal.valueOf(59.99));

        gameResponseDto = new GameResponseDto();
        gameResponseDto.setId(1L);
        gameResponseDto.setName("Test Game");
        gameResponseDto.setPrice(BigDecimal.valueOf(59.99));
        gameResponseDto.setRating(BigDecimal.valueOf(4.5));

        gameUpdateDto = new GameUpdateDto();
        gameUpdateDto.setId(1L);
        gameUpdateDto.setName("Updated Game");
        gameUpdateDto.setPrice(BigDecimal.valueOf(49.99));
        gameUpdateDto.setDescription("Updated description");
        gameUpdateDto.setGenres(Collections.emptyList());
    }

    @Test
    public void testGetById_Success() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameDtoMapper.gameToGameResponseDto(game)).thenReturn(gameResponseDto);

        GameResponseDto result = gameService.getById(1L);

        assertEquals(gameResponseDto, result);
    }

    @Test
    public void testGetById_NotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.getById(1L));
    }

    @Test
    public void testSave_Success() {
        when(gameDtoMapper.gameRequestDtoTOGame(gameRequestDto)).thenReturn(game);

        gameService.save(gameRequestDto);

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testGetAll_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findAll(PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.getAll(0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testDeleteById_Success() {
        when(gameRepository.existsById(1L)).thenReturn(true);

        gameService.deleteById(1L);

        verify(gameRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteById_NotFound() {
        when(gameRepository.existsById(1L)).thenReturn(false);

        assertThrows(GameNotFoundException.class, () -> gameService.deleteById(1L));
    }

    @Test
    public void testUpdate_Success() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(gameDtoMapper.gameUpdateDtoToGame(gameUpdateDto)).thenReturn(game);

        gameService.update(gameUpdateDto);

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdate_NullId() {
        gameUpdateDto.setId(null);

        assertThrows(GameNotFoundException.class, () -> gameService.update(gameUpdateDto));
    }

    @Test
    public void testFindGamesByDeveloper_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findByDeveloperId(1L, PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.findGamesByDeveloper(1L, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindGamesByDeveloper_NotFound() {
        Page<Game> gamePage = new PageImpl<>(Collections.emptyList());
        when(gameRepository.findByDeveloperId(1L, PageRequest.of(0, 10))).thenReturn(gamePage);

        assertThrows(DevelopersGamesNotFoundException.class, () -> gameService.findGamesByDeveloper(1L, 0, 10));
    }

    @Test
    public void testFindGameByName_Success() {
        when(gameRepository.findByName("Test Game")).thenReturn(Optional.of(game));
        when(gameDtoMapper.gameToGameShortcutResponseDto(game)).thenReturn(new GameShortcutResponseDto());

        GameShortcutResponseDto result = gameService.findGameByName("Test Game");

        assertNotNull(result);
    }

    @Test
    public void testFindGameByName_NotFound() {
        when(gameRepository.findByName("Test Game")).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.findGameByName("Test Game"));
    }

    @Test
    public void testSortGamesByRating_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findAllByOrderByRatingAsc(PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.sortGamesByRating(true, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testSortGamesByPrice_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findAllByOrderByPriceAsc(PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.sortGamesByPrice(true, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindGamesByGenre_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findByGenre(1L, PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.findGamesByGenre(1L, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindGamesInPriceRange_Success() {
        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(game));
        when(gameRepository.findByPriceBetween(BigDecimal.valueOf(50), BigDecimal.valueOf(60), PageRequest.of(0, 10))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = gameService.findGamesInPriceRange(BigDecimal.valueOf(50), BigDecimal.valueOf(60), 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    public void testAddGameToBucket_Success() {
        Bucket bucket = new Bucket();
        bucket.setId(1L);
        bucket.setGames(new ArrayList<>());

        when(bucketRepository.findById(1L)).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        gameService.addGameToBucket(1L, 1L);

        assertTrue(bucket.getGames().contains(game));
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    public void testRemoveGameFromBucket_Success() {
        Bucket bucket = new Bucket();
        bucket.setId(1L);
        bucket.setGames(new ArrayList<>());
        bucket.getGames().add(game);
        when(bucketRepository.findById(1L)).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        gameService.removeGameFromBucket(1L, 1L);

        assertFalse(bucket.getGames().contains(game));
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    public void testAddRating_Success() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(gameRepository.existsRatingByAccountAndGame(1L, 1L)).thenReturn(true);

        gameService.addRating(1L, 1L, BigDecimal.valueOf(5));

        verify(gameRepository, times(1)).addRatingIfNotExists(1L, 1L, BigDecimal.valueOf(5));
    }

    @Test
    public void testAddRating_GameNotFound() {
        when(gameRepository.existsById(1L)).thenReturn(false);

        assertThrows(GameNotFoundException.class, () -> gameService.addRating(1L, 1L, BigDecimal.valueOf(5)));
    }

    @Test
    public void testAddRating_AccountNotFound() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.existsById(1L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> gameService.addRating(1L, 1L, BigDecimal.valueOf(5)));
    }

    @Test
    public void testAddRating_ExistingRating() {
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(gameRepository.existsRatingByAccountAndGame(1L, 1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> gameService.addRating(1L, 1L, BigDecimal.valueOf(5)));
    }
}
