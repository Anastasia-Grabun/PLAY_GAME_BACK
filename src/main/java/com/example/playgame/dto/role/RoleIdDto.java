package com.example.playgame.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleIdDto {
    @NotNull(message = "Role's ID cannot be null")
    private Long id;
}
