package com.example.playgame.dto.mapper;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameDtoMapper {
    GameDtoMapper INSTANCE = Mappers.getMapper(GameDtoMapper.class);

    GameResponseDto gameToGameResponseDto(Game game);

    Game gameRequestDtoTOGame(GameRequestDto gameRequestDto);

    GameShortcutResponseDto gameToGameShortcutResponseDto(Game game);

    Game gameUpdateDtoToGame(GameUpdateDto gameUpdateDto);

    List<GameShortcutResponseDto> gamesToGameShortcutDtos(List<Game> games);

    List<GameResponseDto> gamesToGameResponseDtos(List<Game> games);
}
