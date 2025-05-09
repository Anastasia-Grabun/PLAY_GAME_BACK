package com.example.playgame.service.impl;

import com.example.playgame.dto.bucket.BucketRequestDto;
import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.dto.mapper.BucketDtoMapper;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.exception.BucketNullException;
import com.example.playgame.exception.notfound.BucketNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {
    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private BucketDtoMapper bucketDtoMapper;

    @InjectMocks
    private BucketServiceImpl bucketService;

    private Bucket bucket;
    private Game game;
    private BucketResponseDto bucketResponseDto;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setId(1L);
        game.setName("Test Game");

        bucket = new Bucket();
        bucket.setId(1L);
        bucket.setGames(new ArrayList<>());
        bucket.getGames().add(game);

        bucketResponseDto = new BucketResponseDto();
        bucketResponseDto.setId(1L);
        bucketResponseDto.setGames(new ArrayList<>());
    }


    @Test
    void testGetById() {
        when(bucketRepository.existsById(1L)).thenReturn(true);
        when(bucketRepository.getById(1L)).thenReturn(bucket);
        when(bucketDtoMapper.bucketToBucketResponseDto(bucket)).thenReturn(bucketResponseDto);

        BucketResponseDto result = bucketService.getById(1L);

        assertNotNull(result);
        assertEquals(bucketResponseDto.getId(), result.getId());
        verify(bucketRepository, times(1)).existsById(1L);
        verify(bucketRepository, times(1)).getById(1L);
        verify(bucketDtoMapper, times(1)).bucketToBucketResponseDto(bucket);
    }

    @Test
    void testGetById_BucketNotFoundException() {
        when(bucketRepository.existsById(1L)).thenReturn(false);

        assertThrows(BucketNotFoundException.class, () -> bucketService.getById(1L));
        verify(bucketRepository, times(1)).existsById(1L);
    }

    @Test
    void testSave() {
        BucketRequestDto bucketRequestDto = new BucketRequestDto();
        when(bucketDtoMapper.bucketRequestDtoToBucket(bucketRequestDto)).thenReturn(bucket);

        bucketService.save(bucketRequestDto);

        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    void testSave_BucketNullException() {
        assertThrows(BucketNullException.class, () -> bucketService.save(null));
        verify(bucketRepository, times(0)).save(any());
    }

    @Test
    void testGetBucketByAccountId() {
        when(bucketRepository.findBucketByAccount_Id(1L)).thenReturn(Optional.of(bucket));
        when(bucketDtoMapper.bucketToBucketResponseDto(bucket)).thenReturn(bucketResponseDto);

        BucketResponseDto result = bucketService.getBucketByAccountId(1L);

        assertNotNull(result);
        verify(bucketRepository, times(1)).findBucketByAccount_Id(1L);
        verify(bucketDtoMapper, times(1)).bucketToBucketResponseDto(bucket);
    }

    @Test
    void testGetBucketByAccountId_BucketNotFoundException() {
        when(bucketRepository.findBucketByAccount_Id(1L)).thenReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> bucketService.getBucketByAccountId(1L));
        verify(bucketRepository, times(1)).findBucketByAccount_Id(1L);
    }

    @Test
    void testAddGameToBucket() {
        when(bucketRepository.findById(1L)).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        bucketService.addGameToBucket(1L, 1L);

        verify(bucketRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findById(1L);
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    void testAddGameToBucket_BucketNotFoundException() {
        when(bucketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> bucketService.addGameToBucket(1L, 1L));
        verify(bucketRepository, times(1)).findById(1L);
    }

    @Test
    void testAddGameToBucket_GameNotFoundException() {
        when(bucketRepository.findById(1L)).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> bucketService.addGameToBucket(1L, 1L));
        verify(bucketRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    void testRemoveGameFromBucket() {
        when(bucketRepository.findById(1L)).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        bucketService.removeGameFromBucket(1L, 1L);

        verify(bucketRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findById(1L);
        verify(bucketRepository, times(1)).save(bucket);

        assertFalse(bucket.getGames().contains(game));
    }


    @Test
    void testMoveGamesToBuyList() {
        List<Game> wishlistGames = new ArrayList<>();
        wishlistGames.add(game);

        List<Game> buylistGames = new ArrayList<>();

        Bucket wishlist = new Bucket();
        wishlist.setId(1L);
        wishlist.setGames(wishlistGames);

        Bucket buylist = new Bucket();
        buylist.setId(2L);
        buylist.setGames(buylistGames);

        when(bucketRepository.findByAccountIdAndBucketType(1L, "WISHLIST")).thenReturn(Optional.of(wishlist));
        when(bucketRepository.findByAccountIdAndBucketType(1L, "BUYLIST")).thenReturn(Optional.of(buylist));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        bucketService.moveGamesToBuyList(1L, List.of(1L));

        verify(bucketRepository, times(1)).findByAccountIdAndBucketType(1L, "WISHLIST");
        verify(bucketRepository, times(1)).findByAccountIdAndBucketType(1L, "BUYLIST");
        verify(gameRepository, times(1)).findById(1L);
        verify(bucketRepository, times(2)).save(any(Bucket.class)); // Save both wishlist and buylist
    }

    @Test
    void testMoveGamesToBuyList_BucketNotFoundException() {
        when(bucketRepository.findByAccountIdAndBucketType(1L, "WISHLIST")).thenReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> bucketService.moveGamesToBuyList(1L, List.of(1L)));
    }
}