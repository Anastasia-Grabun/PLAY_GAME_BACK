package com.example.playgame.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDto {
    public String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public ZonedDateTime time;
}
