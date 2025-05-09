package com.example.playgame.service.impl;

import com.example.playgame.dto.topUp.TopUpResponseDto;
import com.example.playgame.dto.mapper.TopUpDtoMapper;
import com.example.playgame.entity.Account;
import com.example.playgame.entity.TopUp;
import com.example.playgame.exception.InsufficientFundsException;
import com.example.playgame.exception.InvalidAmountException;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.TopUpNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.TopUpRepository;
import com.example.playgame.service.TopUpService;
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
public class TopUpServiceImpl implements TopUpService {
    private final TopUpRepository topUpRepository;
    private final TopUpDtoMapper topUpDtoMapper;
    private final AccountRepository accountRepository;

    @Override
    public TopUpResponseDto getTopUpById(Long id) {
        TopUp topUp = topUpRepository.findById(id)
                .orElseThrow(() -> new TopUpNotFoundException(id));

        return topUpDtoMapper.topUpToTopUpDto(topUp);
    }

    @Override
    public List<TopUpResponseDto> getTopUpsByAccountId(Long accountId, int page, int size) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<TopUp> topUps = topUpRepository.findByAccountId(accountId, pageable);

        return topUpDtoMapper.topUpsToTopUpDtos(topUps.getContent());
    }

    @Override
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount to deposit must be greater than zero.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        createTopUp(account, amount);
    }

    @Override
    @Transactional
    public void transferFunds(Long senderAccountId, Long receiverAccountId, BigDecimal amount) {
        Account sender = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new AccountNotFoundException(senderAccountId));

        Account receiver = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new AccountNotFoundException(receiverAccountId));

        //баланс меньше нуля быть не может
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the sender's account");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        createTopUp(receiver, amount);
    }

    private void createTopUp(Account account, BigDecimal amount) {
        TopUp topUp = TopUp.builder()
                .amount(amount)
                .account(account)
                .build();

        topUpRepository.save(topUp);
    }
}

