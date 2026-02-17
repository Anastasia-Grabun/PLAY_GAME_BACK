package com.example.playgame.service;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponseDto getById(Long id);

    AccountResponseDto findByUsername(String username);

    void save(AccountRequestDto accountDto);

    List<AccountShortcutResponseDto> getAll(int limit, int page);

    void deleteById(Long id);

    AccountResponseDto update(AccountUpdateDto accountDto);

    BigDecimal checkBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);
}
