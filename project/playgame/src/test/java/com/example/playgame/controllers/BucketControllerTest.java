package com.example.playgame.controllers;

import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.BucketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BucketControllerTest {

    @Mock
    private BucketService bucketService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BucketController bucketController;

    private BucketResponseDto bucketResponseDto;

    @BeforeEach
    public void setUp() {
        bucketResponseDto = new BucketResponseDto();
        bucketResponseDto.setId(1L);
    }

    @Test
    public void testGetMyWishlist_Success() {
        org.springframework.security.core.userdetails.UserDetails userDetails = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        when(bucketService.getWishlistByAccountId(1L)).thenReturn(bucketResponseDto);
        BucketResponseDto result = bucketController.getMyWishlist(userDetails);
        Assertions.assertEquals(bucketResponseDto, result);
    }

    @Test
    public void testGetMyBuylist_Success() {
        org.springframework.security.core.userdetails.UserDetails userDetails = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        when(bucketService.getBuylistByAccountId(1L)).thenReturn(bucketResponseDto);
        BucketResponseDto result = bucketController.getMyBuylist(userDetails);
        Assertions.assertEquals(bucketResponseDto, result);
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
        org.springframework.security.core.userdetails.UserDetails userDetails = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        List<Long> gameIds = Collections.singletonList(1L);
        bucketController.moveGamesToBuyList(userDetails, gameIds);
        verify(bucketService, times(1)).moveGamesToBuyList(1L, gameIds);
    }
}

