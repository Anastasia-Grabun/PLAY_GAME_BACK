package com.example.playgame.controllers;

import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.service.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BucketControllerTest {
    @Mock
    private BucketService bucketService;

    @InjectMocks
    private BucketController bucketController;

    private BucketResponseDto bucketResponseDto;

    @BeforeEach
    public void setUp() {
        bucketResponseDto = new BucketResponseDto();
        bucketResponseDto.setId(1L);
    }

    @Test
    public void testGetBucketById_Success() {
        when(bucketService.getById(1L)).thenReturn(bucketResponseDto);

        BucketResponseDto result = bucketController.getBucketById(1L);

        assertEquals(bucketResponseDto, result);
    }

    @Test
    public void testGetBucketsByAccountId_Success() {
        when(bucketService.getBucketByAccountId(1L)).thenReturn(bucketResponseDto);

        BucketResponseDto result = bucketController.getBucketsByAccountId(1L);

        assertEquals(bucketResponseDto, result);
    }

    @Test
    public void testAddGameToBucket_Success() {
        bucketController.addGameToBucket(1L, 2L);

        verify(bucketService, times(1)).addGameToBucket(1L, 2L);
    }

    @Test
    public void testRemoveGameFromBucket_Success() {
        bucketController.removeGameFromBucket(1L, 2L);

        verify(bucketService, times(1)).removeGameFromBucket(1L, 2L);
    }

    @Test
    public void testMoveGamesToBuyList_Success() {
        List<Long> gameIds = Collections.singletonList(1L);
        bucketController.moveGamesToBuyList(1L, gameIds);

        verify(bucketService, times(1)).moveGamesToBuyList(1L, gameIds);
    }
}
