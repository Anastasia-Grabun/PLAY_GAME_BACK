package com.example.playgame.dto.purchase;

import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Account cannot be null")
    private AccountShortcutResponseDto owner;

    @NotNull(message = "Game cannot be null")
    private GameShortcutResponseDto game;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    @NotNull(message = "Created date cannot be null")
    private Timestamp date;
}
