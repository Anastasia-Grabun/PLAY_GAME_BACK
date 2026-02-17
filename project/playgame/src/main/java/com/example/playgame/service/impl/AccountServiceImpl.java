package com.example.playgame.service.impl;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.mapper.AccountDtoMapper;
import com.example.playgame.entity.Account;
import com.example.playgame.exception.AccountNullException;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountDtoMapper accountDtoMapper;

    @Override
    public AccountResponseDto getByLogin(String login) {
        Account account = accountRepository.findByCredential_Login(login)
                .orElseThrow(() -> new AccountNotFoundException("Credential login: " + login));
        return accountDtoMapper.accountToAccountResponseDto(account);
    }

    @Override
    public AccountResponseDto getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return accountDtoMapper.accountToAccountResponseDto(account);
    }

    @Override
    public void save(AccountRequestDto accountRequestDto) {
        if (accountRequestDto == null) {
            throw new AccountNullException();
        }
        Account account = accountDtoMapper.accountRequestDtoToAccount(accountRequestDto);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteByLogin(String login) {
        Account account = accountRepository.findByCredential_Login(login)
                .orElseThrow(() -> new AccountNotFoundException("Credential login: " + login));
        accountRepository.deleteById(account.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        accountRepository.deleteById(id);
    }

    @Override
    public AccountResponseDto updateByLogin(String login, AccountUpdateDto accountDto) {
        if (accountDto == null) {
            throw new AccountNullException();
        }
        Account account = accountRepository.findByCredential_Login(login)
                .orElseThrow(() -> new AccountNotFoundException("Credential login: " + login));
        account.setUsername(accountDto.getUsername());
        account.setEmail(accountDto.getEmail());
        accountRepository.save(account);
        return accountDtoMapper.accountToAccountResponseDto(account);
    }

    @Override
    public BigDecimal getBalanceByLogin(String login) {
        Account account = accountRepository.findByCredential_Login(login)
                .orElseThrow(() -> new AccountNotFoundException("Credential login: " + login));
        return account.getBalance();
    }

    @Override
    public BigDecimal checkBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.getBalance();
    }

    @Override
    @Transactional
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}
