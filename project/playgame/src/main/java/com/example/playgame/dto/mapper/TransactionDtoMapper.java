package com.example.playgame.dto.mapper;

import com.example.playgame.dto.transaction.TransactionRequestDto;
import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.dto.transaction.TransactionToChangeStatusDto;
import com.example.playgame.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionDtoMapper {
    TransactionDtoMapper INSTANCE = Mappers.getMapper(TransactionDtoMapper.class);

    List<TransactionResponseDto> transactionsToTransactionResponseDtos(List<Transaction> transactions);

    TransactionResponseDto transactionToTransactionResponseDto(Transaction transaction);

    Transaction transactionRequestDtoToTransaction(TransactionRequestDto transactionRequestDto);

    Transaction transactionToChangeStatusDtoToTransaction(TransactionToChangeStatusDto transactionToChangeStatusDto);
}
