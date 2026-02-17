package com.example.playgame.service;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;

import java.math.BigDecimal;

public interface AccountService {
    /** Текущий пользователь по логину (Credential.login = UserDetails.getUsername()). */
    AccountResponseDto getByLogin(String login);

    /** Просмотр любого аккаунта по ID — только для ADMIN. */
    AccountResponseDto getById(Long id);

    void save(AccountRequestDto accountDto);

    /** Удаление своего аккаунта по логину. */
    void deleteByLogin(String login);

    /** Удаление аккаунта по ID — только для ADMIN. */
    void deleteById(Long id);

    /** Обновление профиля текущего пользователя (аккаунт ищется по логину). */
    AccountResponseDto updateByLogin(String login, AccountUpdateDto accountDto);

    /** Баланс текущего пользователя по логину. */
    BigDecimal getBalanceByLogin(String login);

    /** Баланс любого аккаунта по ID — только для ADMIN. */
    BigDecimal checkBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);
}
