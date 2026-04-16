//package com.example.playgame.controllers;
//
//import com.example.playgame.dto.account.AccountRequestDto;
//import com.example.playgame.dto.account.AccountResponseDto;
//import com.example.playgame.dto.account.AccountUpdateDto;
//import com.example.playgame.dto.game.GameShortcutResponseDto;
//import com.example.playgame.service.AccountService;
//import com.example.playgame.service.AuthService;
//import com.example.playgame.service.FavouriteGenreService;
//import com.example.playgame.service.RecommendationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AccountControllerTest {
//    @Mock
//    private AccountService accountService;
//
//    @Mock
//    private AuthService authService;
//
//    @Mock
//    private RecommendationService recommendationService;
//
//    @Mock
//    private FavouriteGenreService favouriteGenreService;
//
//    @Mock
//    private UserDetails userDetails;
//
//    @InjectMocks
//    private AccountsController accountsController;
//
//    private AccountResponseDto accountResponseDto;
//    private AccountRequestDto accountRequestDto;
//    private AccountUpdateDto accountUpdateDto;
//
//    @BeforeEach
//    public void setUp() {
//        accountResponseDto = new AccountResponseDto();
//        accountResponseDto.setId(1L);
//        accountResponseDto.setUsername("testUser");
//
//        accountRequestDto = new AccountRequestDto();
//        accountRequestDto.setUsername("testUser");
//        accountRequestDto.setEmail("test_123@gmail.com");
//
//        accountUpdateDto = new AccountUpdateDto();
//        accountUpdateDto.setId(1L);
//        accountUpdateDto.setUsername("updatedUser");
//        accountUpdateDto.setEmail("testUpdate_123@gmail.com");
//    }
//
//    @Test
//    public void testGetCurrentAccount_Success() {
//        when(userDetails.getUsername()).thenReturn("login1");
//        when(accountService.getByLogin("login1")).thenReturn(accountResponseDto);
//
//        AccountResponseDto result = accountsController.getCurrentAccount(userDetails);
//
//        assertEquals(accountResponseDto, result);
//    }
//
//    @Test
//    public void testGetAccountById_Admin_Success() {
//        when(accountService.getById(1L)).thenReturn(accountResponseDto);
//
//        AccountResponseDto result = accountsController.getAccountById(1L);
//
//        assertEquals(accountResponseDto, result);
//    }
//
//    @Test
//    public void testUpdateAccount_Success() {
//        when(userDetails.getUsername()).thenReturn("login1");
//        when(accountService.updateByLogin("login1", accountUpdateDto)).thenReturn(accountResponseDto);
//
//        AccountResponseDto result = accountsController.updateAccount(userDetails, accountUpdateDto);
//
//        assertEquals(accountResponseDto, result);
//    }
//
//    @Test
//    public void testDeleteCurrentAccount_Success() {
//        when(userDetails.getUsername()).thenReturn("login1");
//
//        accountsController.deleteCurrentAccount(userDetails);
//
//        verify(accountService, times(1)).deleteByLogin("login1");
//    }
//
//    @Test
//    public void testDeleteAccountById_Admin_Success() {
//        accountsController.deleteAccountById(1L);
//
//        verify(accountService, times(1)).deleteById(1L);
//    }
//
//    @Test
//    public void testGetCurrentBalance_Success() {
//        when(userDetails.getUsername()).thenReturn("login1");
//        when(accountService.getBalanceByLogin("login1")).thenReturn(BigDecimal.valueOf(100));
//
//        BigDecimal result = accountsController.getCurrentBalance(userDetails);
//
//        assertEquals(BigDecimal.valueOf(100), result);
//    }
//
//    @Test
//    public void testGetBalanceByAccountId_Admin_Success() {
//        when(accountService.checkBalance(1L)).thenReturn(BigDecimal.valueOf(100));
//
//        BigDecimal result = accountsController.getBalanceByAccountId(1L);
//
//        assertEquals(BigDecimal.valueOf(100), result);
//    }
//
//    @Test
//    public void testUpdateAccountBalance_Success() {
//        accountsController.updateAccountBalance(1L, BigDecimal.valueOf(200));
//
//        verify(accountService, times(1)).updateBalance(1L, BigDecimal.valueOf(200));
//    }
//
//    @Test
//    public void testGetRecommendationsForUser_Success() {
//        List<GameShortcutResponseDto> recommendations = Collections.singletonList(new GameShortcutResponseDto());
//        when(userDetails.getUsername()).thenReturn("user");
//        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
//        when(recommendationService.getRecommendations(1L, 10, 0)).thenReturn(recommendations);
//
//        List<GameShortcutResponseDto> result = accountsController.getRecommendationsForUser(userDetails, 10, 0);
//
//        assertEquals(recommendations, result);
//    }
//
//    @Test
//    public void testAddToFavouritesFromToken_Success() {
//        List<Long> genreIds = List.of(1L, 2L);
//        when(userDetails.getUsername()).thenReturn("user");
//        when(authService.getAccountIdByLogin("user")).thenReturn(1L);
//
//        accountsController.addToFavouritesFromToken(userDetails, genreIds);
//
//        verify(favouriteGenreService, times(1)).addToFavourites(1L, genreIds);
//    }
//
//}
