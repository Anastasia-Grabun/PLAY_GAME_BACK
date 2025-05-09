package com.example.playgame.controllers;

import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionResponseDto transactionResponseDto;

    @BeforeEach
    public void setUp() {
        transactionResponseDto = new TransactionResponseDto();
        transactionResponseDto.setId(1L);
        transactionResponseDto.setAmount(100.0);
    }

    @Test
    public void testGetTransactionById_Success() {
        when(transactionService.getById(1L)).thenReturn(transactionResponseDto);

        TransactionResponseDto result = transactionController.getTransactionById(1L);

        assertEquals(transactionResponseDto, result);
    }

    @Test
    public void testGetAllTransactionsByAccountId_Success() {
        List<TransactionResponseDto> transactions = Collections.singletonList(transactionResponseDto);
        when(transactionService.getAllByAccountId(1L, 0, 10)).thenReturn(transactions);

        List<TransactionResponseDto> result = transactionController.getAllTransactionsByAccountId(1L, 0, 10);

        assertEquals(transactions, result);
    }
}
