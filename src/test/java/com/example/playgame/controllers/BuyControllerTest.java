package com.example.playgame.controllers;

import com.example.playgame.service.BuyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BuyControllerTest {
    @Mock
    private BuyService buyService;

    @InjectMocks
    private BuyController buyController;


    @Test
    public void testBuyGame_Success() {
        Long accountId = 1L;
        Long ownerId = 2L;

        buyController.buyGame(accountId, ownerId);

        verify(buyService, times(1)).purchaseGames(accountId, ownerId);
    }
}
