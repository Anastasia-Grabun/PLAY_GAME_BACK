package com.example.playgame.dto.game;

import com.example.playgame.dto.genre.GenreRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotEmpty(message = "Description cannot be empty")
    @Size(min = 10, max = 400, message = "Description must be between 10 and 400 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be a positive value or zero")
    private BigDecimal price;

    @NotNull(message = "Genres cannot be null")
    private List<GenreRequestDto> genres;
}
