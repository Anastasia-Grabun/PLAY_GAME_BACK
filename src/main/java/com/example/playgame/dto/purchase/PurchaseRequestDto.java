package com.example.playgame.dto.purchase;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDto {
    @NotNull(message = "Account cannot be null")
    private AccountWithIdAndUsernameDto account;

    @NotNull(message = "Game cannot be null")
    private GameShortcutResponseDto gameDto;
}
