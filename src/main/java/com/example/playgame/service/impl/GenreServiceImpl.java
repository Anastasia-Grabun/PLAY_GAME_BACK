package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.dto.mapper.GenreDtoMapper;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.exception.notfound.GenreNotFoundException;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.repository.GenreRepository;
import com.example.playgame.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreDtoMapper genreDtoMapper;
    private final GameDtoMapper gameDtoMapper;
    private final GameRepository gameRepository;

    @Override
    public GenreToGetResponseDto getById(Long id) {
        Genre genre =  genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre with such id:" + id + "not found"));
        return genreDtoMapper.genreToGenreResponseDto(genre);
    }

    @Override
    public void save(GenreRequestDto genreDto) {
        Genre newGenre = genreDtoMapper.genreRequestDtoToGenre(genreDto);
        genreRepository.save(newGenre);
    }

    @Override
    public List<GenreToGetResponseDto> getAll() {
        List<Genre> genres = genreRepository.findAll();
        return genreDtoMapper.genresToGenreResponseDtos(genres);
    }

    @Override
    public void deleteById(Long id) {
        if(!genreRepository.existsById(id)){
            throw new GenreNotFoundException(id);
        }

        genreRepository.deleteById(id);
    }

    @Override
    public void update(GenreUpdateDto updatedGenreDto) {
        if(!genreRepository.existsById(updatedGenreDto.getId())){
            throw new GenreNotFoundException(updatedGenreDto.getId());
        }

        Genre updatedGenre = genreDtoMapper.genreUpdateDtoToGenre(updatedGenreDto);
        genreRepository.save(updatedGenre);
    }

    @Override
    public List<GameShortcutResponseDto> findGamesByGenreSortedByRating(Long genreId, int limit, int page) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException(genreId));

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.desc("rating")));

        Page<Game> games = gameRepository.findByGenresContaining(genre, pageable);

        return gameDtoMapper.gamesToGameShortcutDtos(games.getContent());
    }

    @Override
    public List<GameShortcutResponseDto> findGamesByGenresSortedByRating(List<Long> genreIds, int limit, int page) {
        List<Genre> genres = genreRepository.findAllById(genreIds);

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.desc("rating")));

        Page<Game> games = gameRepository.findByGenresIn(genres, pageable);

        return gameDtoMapper.gamesToGameShortcutDtos(games.getContent());
    }
}