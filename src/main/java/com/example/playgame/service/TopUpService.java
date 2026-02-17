package com.example.playgame.service;

import com.example.playgame.dto.topUp.TopUpResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface TopUpService {
    TopUpResponseDto getTopUpById(Long id);

    List<TopUpResponseDto> getTopUpsByAccountId(Long accountId, int page, int size);

    void deposit(Long accountId, BigDecimal amount);

    void transferFunds(Long senderAccountId, Long receiverAccountId, BigDecimal amount);
}
