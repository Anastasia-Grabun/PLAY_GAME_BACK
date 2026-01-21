package com.example.playgame.dto.bucket;

import com.example.playgame.entity.enums.BucketType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketTypeDto {
    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Bucket type cannot be null")
    private BucketType type;
}
