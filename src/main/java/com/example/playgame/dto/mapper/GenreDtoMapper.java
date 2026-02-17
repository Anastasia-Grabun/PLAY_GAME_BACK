package com.example.playgame.dto.mapper;

import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;
import com.example.playgame.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreDtoMapper {
    GenreDtoMapper INSTANCE = Mappers.getMapper(GenreDtoMapper.class);

    Genre genreRequestDtoToGenre(GenreRequestDto genreRequestDto);

    List<GenreToGetResponseDto> genresToGenreResponseDtos(List<Genre> genres);

    GenreToGetResponseDto genreToGenreResponseDto(Genre genres);

    Genre genreUpdateDtoToGenre(GenreUpdateDto genreUpdateDto);
}
