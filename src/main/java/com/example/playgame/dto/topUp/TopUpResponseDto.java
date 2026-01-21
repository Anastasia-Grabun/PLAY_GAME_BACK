package com.example.playgame.dto.topUp;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Top-up date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Timestamp date;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Account ID cannot be null")
    private AccountWithIdAndUsernameDto account;
}
