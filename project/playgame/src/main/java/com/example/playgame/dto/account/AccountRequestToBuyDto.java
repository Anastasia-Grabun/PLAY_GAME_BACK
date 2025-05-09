package com.example.playgame.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestToBuyDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Balance cannot be null")
    private BigDecimal balance;
}
