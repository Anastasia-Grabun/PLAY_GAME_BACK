package com.example.playgame.dto.mapper;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameDtoMapper {
    GameDtoMapper INSTANCE = Mappers.getMapper(GameDtoMapper.class);

    @Mapping(target = "genres", expression = "java(game.getGenres() != null ? game.getGenres().stream().map(g -> new com.example.playgame.dto.genre.GenreShortDto(g.getId(), g.getName())).collect(java.util.stream.Collectors.toList()) : java.util.Collections.<com.example.playgame.dto.genre.GenreShortDto>emptyList())")
    @Mapping(target = "developerName", expression = "java(game.getDeveloper() != null ? new com.example.playgame.dto.account.AccountWithIdAndUsernameDto(game.getDeveloper().getId(), game.getDeveloper().getUsername()) : null)")
    GameResponseDto gameToGameResponseDto(Game game);

    Game gameRequestDtoTOGame(GameRequestDto gameRequestDto);

    GameShortcutResponseDto gameToGameShortcutResponseDto(Game game);

    Game gameUpdateDtoToGame(GameUpdateDto gameUpdateDto);

    List<GameShortcutResponseDto> gamesToGameShortcutDtos(List<Game> games);

    List<GameResponseDto> gamesToGameResponseDtos(List<Game> games);
}
