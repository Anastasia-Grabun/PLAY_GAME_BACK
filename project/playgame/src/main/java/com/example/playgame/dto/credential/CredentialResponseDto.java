package com.example.playgame.dto.credential;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.dto.role.RoleResponseDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialResponseDto {
    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    private String login;

    @NotNull(message = "Account cannot be null")
    private AccountWithIdAndUsernameDto account;

    @NotNull(message = "Roles cannot be null")
    private List<RoleResponseDto> roles;
}
