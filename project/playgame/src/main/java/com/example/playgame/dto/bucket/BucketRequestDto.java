package com.example.playgame.dto.bucket;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.entity.Account;
import com.example.playgame.entity.enums.BucketType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketRequestDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Account cannot be null")
    private Account account;

    @NotNull(message = "Bucket type cannot be null")
    private BucketType type;

    private List<GameRequestDto> games;
}
