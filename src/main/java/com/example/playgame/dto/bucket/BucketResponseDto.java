package com.example.playgame.dto.bucket;

import com.example.playgame.dto.account.AccountShortcutResponseDto;
import com.example.playgame.dto.game.GameResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Account cannot be null")
    private AccountShortcutResponseDto account;

    private List<GameResponseDto> games;
}
