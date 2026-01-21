package com.example.playgame.dto.mapper;

import com.example.playgame.dto.account.*;
import com.example.playgame.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountDtoMapper {
    AccountDtoMapper INSTANCE = Mappers.getMapper(AccountDtoMapper.class);

    AccountShortcutResponseDto accountToAccountShortcutResponseDto(Account account);

    AccountResponseDto accountToAccountResponseDto(Account account);

    Account accountRequestDtoToAccount(AccountRequestDto accountRequestDto);

    List<AccountShortcutResponseDto> accountsToAccountShortcutResponseDtos(List<Account> accounts);

    Account accountRequestToBuyDtoToAccount(AccountRequestToBuyDto accountRequestToBuyDto);

    Account accountWithIdAndUsernameDtoToAccount(AccountWithIdAndUsernameDto account);
}

