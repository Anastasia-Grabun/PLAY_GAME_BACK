package com.example.playgame.service.impl;

import com.example.playgame.entity.Account;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Purchase;
import com.example.playgame.entity.Transaction;
import com.example.playgame.entity.enums.BucketType;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.BucketNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.PurchaseRepository;
import com.example.playgame.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyServiceTest {
    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private BuyServiceImpl buyService;

    private Account account;
    private Bucket bucket;
    private Game game;

    @BeforeEach
    public void setUp() {
        account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));

        game = new Game();
        game.setId(1L);
        game.setPrice(BigDecimal.valueOf(50));

        bucket = new Bucket();
        bucket.setGames(Collections.singletonList(game));
    }

    @Test
    public void testPurchaseGames_Success() {
        account.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString())).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        Account owner = new Account();
        owner.setId(2L);
        owner.setBalance(BigDecimal.valueOf(100));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(owner));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        buyService.purchaseGames(1L, 2L);

        assertEquals(BigDecimal.valueOf(50), account.getBalance());

        verify(purchaseRepository, times(1)).save(any(Purchase.class));

        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    public void testPurchaseGames_InsufficientFunds() {
        account.setBalance(BigDecimal.valueOf(30));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString())).thenReturn(Optional.of(bucket));

        buyService.purchaseGames(1L, 2L);

        assertEquals(BigDecimal.valueOf(30), account.getBalance());
        verify(purchaseRepository, never()).save(any(Purchase.class));
        verify(bucketRepository, never()).save(bucket);
    }

    @Test
    public void testPurchaseGames_BucketNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString())).thenReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> buyService.purchaseGames(1L, 2L));
    }

    @Test
    public void testPurchaseGames_GameNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString())).thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> buyService.purchaseGames(1L, 2L));
    }

    @Test
    public void testPurchaseGames_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> buyService.purchaseGames(1L, 2L));
    }
}
