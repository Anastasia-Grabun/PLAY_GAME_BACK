package com.example.playgame.service.impl;

import com.example.playgame.entity.Account;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Purchase;
import com.example.playgame.entity.Transaction;
import com.example.playgame.entity.enums.BucketType;
import com.example.playgame.entity.enums.TransactionStatus;
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
import java.util.List;
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

    private Account buyer;
    private Account owner;
    private Bucket bucket;
    private Game game;

    @BeforeEach
    public void setUp() {
        buyer = new Account();
        buyer.setId(1L);
        buyer.setBalance(BigDecimal.valueOf(100));

        owner = new Account();
        owner.setId(1L);

        game = new Game();
        game.setId(1L);
        game.setPrice(BigDecimal.valueOf(50));

        bucket = new Bucket();
        bucket.setGames(List.of(game));
    }

    @Test
    public void shouldPurchaseGamesSuccessfully() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString()))
                .thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        buyService.purchaseGames(String.valueOf(1L));

        assertEquals(BigDecimal.valueOf(50), buyer.getBalance());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    public void shouldNotPurchase_WhenInsufficientFunds() {
        buyer.setBalance(BigDecimal.valueOf(10));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString()))
                .thenReturn(Optional.of(bucket));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            tx.setStatus(TransactionStatus.FAILED);
            return tx;
        });

        buyService.purchaseGames(String.valueOf(1L));

        assertEquals(BigDecimal.valueOf(10), buyer.getBalance());
        verify(purchaseRepository, never()).save(any(Purchase.class));
        verify(bucketRepository, never()).save(any(Bucket.class));
    }

    @Test
    public void shouldThrowException_WhenBucketNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString()))
                .thenReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> buyService.purchaseGames(String.valueOf(1L)));

    }

    @Test
    public void shouldThrowException_WhenGameNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(bucketRepository.findByAccountIdAndBucketType(1L, BucketType.BUYLIST.toString()))
                .thenReturn(Optional.of(bucket));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> buyService.purchaseGames(String.valueOf(1L)));
        ;
    }

    @Test
    public void shouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> buyService.purchaseGames(String.valueOf(1L)));
    }
}
