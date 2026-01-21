package com.example.playgame.dto.credential;

import com.example.playgame.dto.role.RoleRequestDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialResponseByRoleDto {
    @NotNull(message = "Login cannot be null")
    @Size(min = 1, max = 20, message = "Login must be between 1 and 10 characters")
    private String login;

    @NotNull(message = "Roles cannot be null")
    private List<RoleRequestDto> roles;
}
