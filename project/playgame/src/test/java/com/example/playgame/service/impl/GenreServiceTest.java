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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreDtoMapper genreDtoMapper;

    @Mock
    private GameDtoMapper gameDtoMapper;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;
    private GenreRequestDto genreRequestDto;
    private GenreToGetResponseDto genreResponseDto;
    private GenreUpdateDto genreUpdateDto;

    @BeforeEach
    public void setUp() {
        genre = new Genre();
        genre.setId(1L);
        genre.setName("Action");

        genreRequestDto = new GenreRequestDto();
        genreRequestDto.setName("Action");

        genreResponseDto = new GenreToGetResponseDto();
        genreResponseDto.setId(1L);
        genreResponseDto.setName("Action");

        genreUpdateDto = new GenreUpdateDto();
        genreUpdateDto.setId(1L);
        genreUpdateDto.setName("Updated Action");
    }

    @Test
    public void testGetById_Success() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreDtoMapper.genreToGenreResponseDto(genre)).thenReturn(genreResponseDto);

        GenreToGetResponseDto result = genreService.getById(1L);

        assertEquals(genreResponseDto, result);
    }

    @Test
    public void testGetById_NotFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.getById(1L));
    }

    @Test
    public void testSave_Success() {
        when(genreDtoMapper.genreRequestDtoToGenre(genreRequestDto)).thenReturn(genre);

        genreService.save(genreRequestDto);

        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    public void testGetAll_Success() {
        List<Genre> genres = Collections.singletonList(genre);
        when(genreRepository.findAll()).thenReturn(genres);
        when(genreDtoMapper.genresToGenreResponseDtos(genres)).thenReturn(Collections.singletonList(genreResponseDto));

        List<GenreToGetResponseDto> result = genreService.getAll();

        assertEquals(1, result.size());
        assertEquals(genreResponseDto, result.get(0));
    }

    @Test
    public void testDeleteById_Success() {
        when(genreRepository.existsById(1L)).thenReturn(true);

        genreService.deleteById(1L);

        verify(genreRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteById_NotFound() {
        when(genreRepository.existsById(1L)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> genreService.deleteById(1L));
    }

    @Test
    public void testUpdate_Success() {
        when(genreRepository.existsById(1L)).thenReturn(true);
        when(genreDtoMapper.genreUpdateDtoToGenre(genreUpdateDto)).thenReturn(genre);

        genreService.update(genreUpdateDto);

        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    public void testUpdate_NotFound() {
        when(genreRepository.existsById(1L)).thenReturn(false);

        assertThrows(GenreNotFoundException.class, () -> genreService.update(genreUpdateDto));
    }

    @Test
    public void testFindGamesByGenreSortedByRating_Success() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(new Game()));
        when(gameRepository.findByGenresContaining(any(Genre.class), any(Pageable.class))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = genreService.findGamesByGenreSortedByRating(1L, 10, 0);

        assertEquals(1, result.size());
    }

    @Test
    public void testFindGamesByGenreSortedByRating_NotFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findGamesByGenreSortedByRating(1L, 10, 0));
    }

    @Test
    public void testFindGamesByGenresSortedByRating_Success() {
        when(genreRepository.findAllById(Collections.singletonList(1L))).thenReturn(Collections.singletonList(genre));

        Page<Game> gamePage = new PageImpl<>(Collections.singletonList(new Game()));
        when(gameRepository.findByGenresIn(anyList(), any(Pageable.class))).thenReturn(gamePage);
        when(gameDtoMapper.gamesToGameShortcutDtos(gamePage.getContent())).thenReturn(Collections.singletonList(new GameShortcutResponseDto()));

        List<GameShortcutResponseDto> result = genreService.findGamesByGenresSortedByRating(Collections.singletonList(1L), 10, 0);

        assertEquals(1, result.size());
    }
}
