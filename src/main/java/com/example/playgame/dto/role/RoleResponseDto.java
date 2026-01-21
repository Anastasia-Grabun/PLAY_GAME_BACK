package com.example.playgame.dto.role;

import com.example.playgame.entity.enums.Roles;
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
public class RoleResponseDto {
    @NotNull(message = "Role's ID cannot be null")
    private Long id;

    @NotNull(message = "Role's name cannot be null")
    private Roles name;
}
