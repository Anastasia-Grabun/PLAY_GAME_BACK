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
import com.example.playgame.service.BuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BuyServiceImpl implements BuyService {
    private final PurchaseRepository purchaseRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BucketRepository bucketRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purchaseGames(Long accountId, Long ownerId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException(accountId));

        Bucket buylist = getBuylist(accountId);

        BigDecimal totalAmount = calculateTotalAmount(buylist);

        Transaction transaction = createTransaction(account, totalAmount);

        if (transaction.getStatus() == TransactionStatus.FAILED) {
            return;
        }

        for (Game game : buylist.getGames()) {
            gameRepository.findById(game.getId()).orElseThrow(() -> new GameNotFoundException(game.getId()));
        }

        savePurchases(buylist.getGames(), ownerId, transaction);

        clearBuylist(buylist);

        updateAccountBalance(account, totalAmount);
    }

    private Bucket getBuylist(Long accountId) {
        return bucketRepository.findByAccountIdAndBucketType(accountId, BucketType.BUYLIST.toString())
                .orElseThrow(()-> new BucketNotFoundException("Bucket not found"));
    }

    private BigDecimal calculateTotalAmount(Bucket buylist) {
        return buylist.getGames().stream()
                .filter(Objects::nonNull)
                .map(Game::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Transaction createTransaction(Account account, BigDecimal totalAmount) {
        TransactionStatus status = determineTransactionStatus(account, totalAmount);
        return createTransactionWithStatus(account, totalAmount, status);
    }

    private TransactionStatus determineTransactionStatus(Account account, BigDecimal totalAmount) {
        if (account.getBalance().compareTo(totalAmount) < 0) {
            return TransactionStatus.FAILED;
        }
        return TransactionStatus.COMPLETED;
    }

    private Transaction createTransactionWithStatus(Account account, BigDecimal totalAmount, TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setAmount(totalAmount.doubleValue());
        transaction.setStatus(status);
        transaction.setAccount(account);

        transactionRepository.save(transaction);

        return transaction;
    }

    private void savePurchases(List<Game> games, Long ownerId, Transaction transaction) {
        for (Game game : games) {
            Purchase purchase = new Purchase();

            Account owner = accountRepository.findById(ownerId)
                    .orElseThrow(() -> new AccountNotFoundException(ownerId));

            purchase.setOwner(owner);
            purchase.setGame(game);
            purchase.setTransaction(transaction);

            purchaseRepository.save(purchase);
        }
    }

    private void clearBuylist(Bucket buylist) {
        List<Game> games = new ArrayList<>(buylist.getGames());
        games.clear();
        buylist.setGames(games);
        bucketRepository.save(buylist);
    }

    private void updateAccountBalance(Account account, BigDecimal totalAmount) {
        account.setBalance(account.getBalance().subtract(totalAmount));
        accountRepository.save(account);
    }
}
