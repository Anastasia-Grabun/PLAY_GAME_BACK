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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository accountRepository;

    @Mock
    private AccountDtoMapper accountDtoMapper;

    @InjectMocks
    AccountServiceImpl accountService;

    private Account account;
    private AccountRequestDto accountRequestDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountResponseDto accountResponseDto;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setUsername("testuser");
        account.setEmail("testuser@example.com");
        account.setBalance(BigDecimal.valueOf(100.00));
        account.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setUsername("testuser");
        accountRequestDto.setEmail("testuser@example.com");

        accountUpdateDto = new AccountUpdateDto();
        accountUpdateDto.setId(1L);
        accountUpdateDto.setUsername("updateduser");
        accountUpdateDto.setEmail("updateduser@example.com");

        accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(1L);
        accountResponseDto.setUsername("updateduser");
        accountResponseDto.setEmail("updateduser@example.com");
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        when(accountDtoMapper.accountToAccountResponseDto(any(Account.class)))
                .thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.getById(1L);

        assertNotNull(result);
        assertEquals(accountResponseDto, result);

        verify(accountRepository, times(1)).findById(1L);
        verify(accountDtoMapper, times(1)).accountToAccountResponseDto(account);
    }

    @Test
    void testGetAccountById_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getById(1L);
        });

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountByUsername() {
        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));

        when(accountDtoMapper.accountToAccountResponseDto(any(Account.class)))
                .thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals(accountResponseDto, result);

        verify(accountRepository, times(1)).findByUsername("testuser");
        verify(accountDtoMapper, times(1)).accountToAccountResponseDto(account);
    }

    @Test
    void testFindAccountByUsername_AccountNotFound() {
        when(accountRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> {
            accountService.findByUsername("testuser");
        });

        verify(accountRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testSave() {
        when(accountDtoMapper.accountRequestDtoToAccount(accountRequestDto)).thenReturn(account);

        accountService.save(accountRequestDto);

        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testSave_AccountNullException() {
        assertThrows(AccountNullException.class, () -> accountService.save(null));
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> page = new PageImpl<>(List.of(account));

        when(accountRepository.findAll(pageable)).thenReturn(page);

        AccountShortcutResponseDto accountShortcutResponseDto = new AccountShortcutResponseDto();
        accountShortcutResponseDto.setUsername(account.getUsername());
        accountShortcutResponseDto.setEmail(account.getEmail());

        when(accountDtoMapper.accountsToAccountShortcutResponseDtos(anyList()))
                .thenReturn(List.of(accountShortcutResponseDto));

        var result = accountService.getAll(10, 0);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(accountRepository, times(1)).findAll(pageable);
        verify(accountDtoMapper, times(1)).accountsToAccountShortcutResponseDtos(Mockito.anyList());
    }

    @Test
    void testDeleteById() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        accountService.deleteById(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_AccountNotFoundException() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> accountService.deleteById(1L));

        verify(accountRepository, times(1)).existsById(1L);
    }

    @Test
    void testUpdate() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountDtoMapper.accountToAccountResponseDto(any(Account.class))).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.update(accountUpdateDto);

        assertNotNull(result);
        assertEquals(accountResponseDto.getUsername(), result.getUsername());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testUpdate_AccountNullException() {
        assertThrows(AccountNullException.class, () -> accountService.update(null));
        assertThrows(AccountNullException.class, () -> accountService.update(new AccountUpdateDto()));
    }

    @Test
    void testUpdate_AccountNotFoundException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.update(accountUpdateDto));
    }

    @Test
    void testCheckBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BigDecimal balance = accountService.checkBalance(1L);

        assertNotNull(balance);
        assertEquals(account.getBalance(), balance);
    }

    @Test
    void testCheckBalance_AccountNotFoundException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.checkBalance(1L));
    }

    @Test
    void testUpdateBalance() {
        BigDecimal newBalance = BigDecimal.valueOf(200.00);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.updateBalance(1L, newBalance);

        assertEquals(newBalance, account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testUpdateBalance_AccountNotFoundException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.updateBalance(1L, BigDecimal.valueOf(200.00)));
    }
}
