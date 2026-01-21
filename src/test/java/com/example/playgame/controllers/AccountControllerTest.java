package com.example.playgame.controllers;

import com.example.playgame.dto.account.AccountRequestDto;
import com.example.playgame.dto.account.AccountResponseDto;
import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.account.AccountUpdateDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.service.AccountService;
import com.example.playgame.service.FavouriteGenreService;
import com.example.playgame.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    @Mock
    private AccountService accountService;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private FavouriteGenreService favouriteGenreService;

    @InjectMocks
    private AccountsController accountsController;

    private AccountResponseDto accountResponseDto;
    private AccountRequestDto accountRequestDto;
    private AccountUpdateDto accountUpdateDto;

    @BeforeEach
    public void setUp() {
        accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(1L);
        accountResponseDto.setUsername("testUser ");

        accountRequestDto = new AccountRequestDto();
        accountRequestDto.setUsername("testUser");
        accountRequestDto.setEmail("test_123@gmail.com");

        accountUpdateDto = new AccountUpdateDto();
        accountUpdateDto.setId(1L);
        accountUpdateDto.setUsername("updatedUser");
        accountUpdateDto.setEmail("testUpdate_123@gmail.com");
    }

    @Test
    public void testGetAccountById_Success() {
        when(accountService.getById(1L)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountsController.getAccountById(1L);

        assertEquals(accountResponseDto, result);
    }

    @Test
    public void testGetAccountByUsername_Success() {
        when(accountService.findByUsername("testUser ")).thenReturn(accountResponseDto);

        AccountResponseDto result = accountsController.getAccountByUsername("testUser ");

        assertEquals(accountResponseDto, result);
    }

    @Test
    public void testGetAllAccounts_Success() {
        List<AccountShortcutResponseDto> accounts = Collections.singletonList(new AccountShortcutResponseDto());
        when(accountService.getAll(10, 0)).thenReturn(accounts);

        List<AccountShortcutResponseDto> result = accountsController.getAllAccounts(0, 10);

        assertEquals(accounts, result);
    }

    @Test
    public void testUpdateAccount_Success() {
        when(accountService.update(accountUpdateDto)).thenReturn(accountResponseDto);

        AccountResponseDto result = accountsController.updateAccount(accountUpdateDto);

        assertEquals(accountResponseDto, result);
    }

    @Test
    public void testDeleteAccount_Success() {
        accountsController.deleteAccount(1L);

        verify(accountService, times(1)).deleteById(1L);
    }

    @Test
    public void testGetBalance_Success() {
        when(accountService.checkBalance(1L)).thenReturn(BigDecimal.valueOf(100));

        BigDecimal result = accountsController.getBalance(1L);

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    public void testUpdateAccountBalance_Success() {
        accountsController.updateAccountBalance(1L, BigDecimal.valueOf(200));

        verify(accountService, times(1)).updateBalance(1L, BigDecimal.valueOf(200));
    }

    @Test
    public void testGetRecommendationsForUser_Success() {
        List<GameShortcutResponseDto> recommendations = Collections.singletonList(new GameShortcutResponseDto());
        when(recommendationService.getRecommendationsForUser (1L, 10, 0)).thenReturn(recommendations);

        List<GameShortcutResponseDto> result = accountsController.getRecommendationsForUser (1L, 10, 0);

        assertEquals(recommendations, result);
    }

    @Test
    public void testAddToFavouritesFromToken_Success() {
        String token = "Bearer mock-jwt-token";
        List<Long> genreIds = List.of(1L, 2L);

        accountsController.addToFavouritesFromToken(token, genreIds);

        verify(favouriteGenreService, times(1))
                .addToFavouritesUsingToken(token, genreIds);
    }

}
