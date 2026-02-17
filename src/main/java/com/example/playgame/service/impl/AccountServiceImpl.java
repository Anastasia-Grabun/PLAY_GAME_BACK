package com.example.playgame.service.impl;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.mapper.AccountDtoMapper;
import com.example.playgame.entity.Account;
import com.example.playgame.exception.AccountNullException;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountDtoMapper accountDtoMapper;

    @Override
    public AccountResponseDto getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return accountDtoMapper.accountToAccountResponseDto(account);
    }

    @Override
    public AccountResponseDto findByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Username: " + username));

        return accountDtoMapper.accountToAccountResponseDto(account);
    }

    public void save(AccountRequestDto accountRequestDto) {
        if (accountRequestDto == null) {
            throw new AccountNullException();
        }

        Account account = accountDtoMapper.accountRequestDtoToAccount(accountRequestDto);

        accountRepository.save(account);
    }

    @Override
    public List<AccountShortcutResponseDto> getAll(int limit, int page) {
        Pageable pageable = PageRequest.of(page, limit);

        Page<Account> accountsPage = accountRepository.findAll(pageable);

        return accountDtoMapper.accountsToAccountShortcutResponseDtos(accountsPage.getContent());
    }


    @Override
    public void deleteById(Long id) {
        if(!accountRepository.existsById(id)){
            throw new AccountNotFoundException(id);
        }

        accountRepository.deleteById(id);
    }

    @Override
    public AccountResponseDto update(AccountUpdateDto accountDto) {
        if (accountDto == null || accountDto.getId() == null) {
            throw new AccountNullException();
        }

        Account account = accountRepository.findById(accountDto.getId())
                .orElseThrow(() -> new AccountNotFoundException(accountDto.getId()));

        account.setUsername(accountDto.getUsername());
        account.setEmail(accountDto.getEmail());

        accountRepository.save(account);

        return accountDtoMapper.accountToAccountResponseDto(account);
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
