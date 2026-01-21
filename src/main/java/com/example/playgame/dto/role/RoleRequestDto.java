package com.example.playgame.dto.role;

import com.example.playgame.entity.enums.Roles;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {
    @NotNull(message = "Role's ID cannot be null")
    private Long id;

    @NotNull(message = "Role's name cannot be null")
    private Roles name;
}
