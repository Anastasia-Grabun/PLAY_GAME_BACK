package com.example.playgame.service.impl;

import com.example.playgame.dto.mapper.TransactionDtoMapper;
import com.example.playgame.dto.transaction.TransactionRequestDto;
import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.entity.Transaction;
import com.example.playgame.exception.notfound.TransactionNotFoundException;
import com.example.playgame.repository.TransactionRepository;
import com.example.playgame.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDtoMapper transactionDtoMapper;

    @Override
    public TransactionResponseDto getById(Long id) {
        if(!transactionRepository.existsById(id)){
            throw new TransactionNotFoundException(id);
        }

        Transaction transaction = transactionRepository.getById(id);

        return transactionDtoMapper.transactionToTransactionResponseDto(transaction);
    }

    @Override
    public void save(TransactionRequestDto transactionDto) {
        if (transactionDto == null) {
            throw new TransactionNotFoundException("Transaction cannot be null");
        }

        Transaction transaction = transactionDtoMapper.transactionRequestDtoToTransaction(transactionDto);

        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionResponseDto> getAllByAccountId(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions = transactionRepository.findAllByAccount_Id(accountId, pageable);

        return transactionDtoMapper.transactionsToTransactionResponseDtos(transactions. getContent());
    }
}