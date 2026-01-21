package com.example.playgame.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotNull(message = "Login cannot be null")
    @Size(min = 1, max = 20, message = "Login must be between 1 and 10 characters")
    private String login;

    @NotNull(message = "Password cannot be null")
    @Size(min = 10, max = 10, message = "Password must be exactly 10 characters long")
    private String password;
}
