package com.example.playgame.service;

import com.example.playgame.dto.transaction.TransactionRequestDto;
import com.example.playgame.dto.transaction.TransactionResponseDto;

import java.util.List;

public interface TransactionService {
    TransactionResponseDto getById(Long id);

    void save(TransactionRequestDto transactionDto);

    List<TransactionResponseDto> getAllByAccountId(Long accountId, int page, int size);
}
