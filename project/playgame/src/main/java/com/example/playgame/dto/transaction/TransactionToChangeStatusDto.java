package com.example.playgame.dto.transaction;

import com.example.playgame.entity.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionToChangeStatusDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Status cannot be null")
    private TransactionStatus status;
}
