package com.example.playgame.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Login cannot be null")
    @Size(min = 1, max = 10, message = "Login must be between 1 and 10 characters")
    private String login;

    @NotNull(message = "Password cannot be null")
    @Size(min = 10, max = 10, message = "Password must be exactly 10 characters long")
    private String password;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
}
