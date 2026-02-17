package com.example.playgame.controllers;

import com.example.playgame.dto.topUp.TopUpResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.TopUpService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.core.userdetails.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopUpControllerTest {
    @Mock
    private TopUpService topUpService;

    @Mock
    private AuthService authService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TopUpController topUpController;

    private TopUpResponseDto topUpResponseDto;

    @BeforeEach
    public void setUp() {
        topUpResponseDto = new TopUpResponseDto();
        topUpResponseDto.setId(1L);
        topUpResponseDto.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    public void testGetTopUpById_Success() {
        when(topUpService.getTopUpById(1L)).thenReturn(topUpResponseDto);

        TopUpResponseDto result = topUpController.getTopUpById(1L);

        assertEquals(topUpResponseDto, result);
    }

    @Test
    public void testGetTopUpsByAccountId_Success() {
        List<TopUpResponseDto> topUps = Collections.singletonList(topUpResponseDto);
        when(topUpService.getTopUpsByAccountId(1L, 0, 10)).thenReturn(topUps);

        List<TopUpResponseDto> result = topUpController.getTopUpsByAccountId(1L, 0, 10);

        assertEquals(topUps, result);
    }

    @Test
    public void testDeposit_Success() {
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        topUpController.deposit(userDetails, BigDecimal.valueOf(100));
        verify(topUpService, times(1)).deposit(1L, BigDecimal.valueOf(100));
    }

    @Test
    public void testTransferFunds_Success() {
        when(userDetails.getUsername()).thenReturn("user");
        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
        Long receiverAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(50);
        topUpController.transferFunds(userDetails, receiverAccountId, amount);
        verify(topUpService, times(1)).transferFunds(1L, receiverAccountId, amount);
    }
}
