package com.example.playgame.dto.transaction;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.entity.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Date cannot be null")
    private Date time;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Amount must be a positive value or zero")
    private Double amount;

    @NotNull(message = "Status cannot be null")
    private TransactionStatus status;

    @NotNull(message = "Account cannot be null")
    private AccountWithIdAndUsernameDto account;
}
