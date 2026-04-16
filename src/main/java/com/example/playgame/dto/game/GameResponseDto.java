package com.example.playgame.dto.game;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.dto.genre.GenreShortDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Date cannot be null")
    private Date date;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be a positive value or zero")
    private BigDecimal price;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 0, message = "Rating must be between 0 and 5")
    @Max(value = 5, message = "Rating must be between 0 and 5")
    private BigDecimal rating;

    private List<GenreShortDto> genres;

    private AccountWithIdAndUsernameDto developerName;

    private String coverImageUrl;
}
