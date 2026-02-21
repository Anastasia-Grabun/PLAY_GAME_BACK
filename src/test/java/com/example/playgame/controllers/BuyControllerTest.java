package com.example.playgame.controllers;

import com.example.playgame.service.AuthService;
import com.example.playgame.service.BuyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyControllerTest {
    @Mock
    private BuyService buyService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BuyController buyController;

    @Test
    public void testCheckout_Success() {
        org.springframework.security.core.userdetails.UserDetails userDetails = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        buyController.checkout(userDetails);
        verify(buyService, times(1)).purchaseGames(1L);
    }

}
