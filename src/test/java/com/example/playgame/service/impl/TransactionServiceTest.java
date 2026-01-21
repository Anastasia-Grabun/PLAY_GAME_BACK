package com.example.playgame.service.impl;

import com.example.playgame.dto.mapper.TransactionDtoMapper;
import com.example.playgame.dto.transaction.TransactionRequestDto;
import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.entity.Transaction;
import com.example.playgame.exception.notfound.TransactionNotFoundException;
import com.example.playgame.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDtoMapper transactionDtoMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private TransactionResponseDto transactionResponseDto;
    private TransactionRequestDto transactionRequestDto;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0);

        transactionResponseDto = new TransactionResponseDto();
        transactionResponseDto.setId(1L);
        transactionResponseDto.setAmount(100.0);

        transactionRequestDto = new TransactionRequestDto();
        transactionRequestDto.setAmount(100.0);
    }

    @Test
    public void testGetById_Success() {
        when(transactionRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.getById(1L)).thenReturn(transaction);
        when(transactionDtoMapper.transactionToTransactionResponseDto(transaction)).thenReturn(transactionResponseDto);

        TransactionResponseDto result = transactionService.getById(1L);

        assertEquals(transactionResponseDto, result);
    }

    @Test
    public void testGetById_NotFound() {
        when(transactionRepository.existsById(1L)).thenReturn(false);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getById(1L));
    }

    @Test
    public void testSave_Success() {
        when(transactionDtoMapper.transactionRequestDtoToTransaction(transactionRequestDto)).thenReturn(transaction);

        transactionService.save(transactionRequestDto);

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void testSave_NullTransaction() {
        assertThrows(TransactionNotFoundException.class, () -> transactionService.save(null));
    }

    @Test
    public void testGetAllByAccountId_Success() {
        Long accountId = 1L;
        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findAllByAccount_Id(accountId, PageRequest.of(0, 10))).thenReturn(transactionPage);
        when(transactionDtoMapper.transactionsToTransactionResponseDtos(transactionPage.getContent())).thenReturn(Collections.singletonList(transactionResponseDto));

        List<TransactionResponseDto> result = transactionService.getAllByAccountId(accountId, 0, 10);

        assertEquals(1, result.size());
        assertEquals(transactionResponseDto, result.get(0));
    }
}
