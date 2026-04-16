package com.example.playgame.service.impl;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
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
    void testGetByLogin() {
        when(accountRepository.findByCredential_Login("testuser")).thenReturn(Optional.of(account));
        when(accountDtoMapper.accountToAccountResponseDto(any(Account.class))).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.getByLogin("testuser");

        assertNotNull(result);
        assertEquals(accountResponseDto, result);
        verify(accountRepository, times(1)).findByCredential_Login("testuser");
        verify(accountDtoMapper, times(1)).accountToAccountResponseDto(account);
    }

    @Test
    void testGetByLogin_AccountNotFound() {
        when(accountRepository.findByCredential_Login("testuser")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getByLogin("testuser"));

        verify(accountRepository, times(1)).findByCredential_Login("testuser");
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
    void testDeleteByLogin() {
        when(accountRepository.findByCredential_Login("testuser")).thenReturn(Optional.of(account));

        accountService.deleteByLogin("testuser");

        verify(accountRepository, times(1)).findByCredential_Login("testuser");
        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByLogin_AccountNotFound() {
        when(accountRepository.findByCredential_Login("unknown")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.deleteByLogin("unknown"));

        verify(accountRepository, times(1)).findByCredential_Login("unknown");
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
    void testUpdateByLogin() {
        when(accountRepository.findByCredential_Login("testuser")).thenReturn(Optional.of(account));
        when(accountDtoMapper.accountToAccountResponseDto(any(Account.class))).thenReturn(accountResponseDto);

        AccountResponseDto result = accountService.updateByLogin("testuser", accountUpdateDto);

        assertNotNull(result);
        assertEquals(accountResponseDto.getUsername(), result.getUsername());
        verify(accountRepository, times(1)).findByCredential_Login("testuser");
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testUpdateByLogin_AccountNullException() {
        assertThrows(AccountNullException.class, () -> accountService.updateByLogin("testuser", null));
    }

    @Test
    void testUpdateByLogin_AccountNotFoundException() {
        when(accountRepository.findByCredential_Login("unknown")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.updateByLogin("unknown", accountUpdateDto));
    }
}
