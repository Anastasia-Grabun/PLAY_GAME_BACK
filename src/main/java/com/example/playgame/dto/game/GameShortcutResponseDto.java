package com.example.playgame.dto.game;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameShortcutResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be a positive value or zero")
    private BigDecimal price;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 0, message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private BigDecimal rating;
}
