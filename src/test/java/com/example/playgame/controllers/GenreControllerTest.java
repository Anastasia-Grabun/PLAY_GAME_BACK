package com.example.playgame.controllers;

import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.genre.GenreRequestDto;
import com.example.playgame.dto.genre.GenreToGetResponseDto;
import com.example.playgame.dto.genre.GenreUpdateDto;
import com.example.playgame.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreControllerTest {
    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private GenreToGetResponseDto genreResponseDto;
    private GenreRequestDto genreRequestDto;
    private GenreUpdateDto genreUpdateDto;

    @BeforeEach
    public void setUp() {
        genreResponseDto = new GenreToGetResponseDto();
        genreResponseDto.setId(1L);
        genreResponseDto.setName("Action");

        genreRequestDto = new GenreRequestDto();
        genreRequestDto.setName("Action");

        genreUpdateDto = new GenreUpdateDto();
        genreUpdateDto.setId(1L);
        genreUpdateDto.setName("Updated Action");
    }

    @Test
    public void testGetGenreById_Success() {
        when(genreService.getById(1L)).thenReturn(genreResponseDto);

        GenreToGetResponseDto result = genreController.getGenreById(1L);

        assertEquals(genreResponseDto, result);
    }

    @Test
    public void testCreateGenre_Success() {
        genreController.createGenre(genreRequestDto);

        verify(genreService, times(1)).save(genreRequestDto);
    }

    @Test
    public void testGetAllGenres_Success() {
        List<GenreToGetResponseDto> genres = Collections.singletonList(genreResponseDto);
        when(genreService.getAll()).thenReturn(genres);

        List<GenreToGetResponseDto> result = genreController.getAllGenres();

        assertEquals(genres, result);
    }

    @Test
    public void testDeleteGenre_Success() {
        genreController.deleteGenre(1L);

        verify(genreService, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateGenre_Success() {
        genreController.updateGenre(genreUpdateDto);

        verify(genreService, times(1)).update(genreUpdateDto);
    }

    @Test
    public void testGetGamesByGenreSortedByRating_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(genreService.findGamesByGenreSortedByRating(1L, 10, 0)).thenReturn(games);

        List<GameShortcutResponseDto> result = genreController.getGamesByGenreSortedByRating(1L, 0, 10);

        assertEquals(games, result);
    }

    @Test
    public void testGetGamesByGenresSortedByRating_Success() {
        List<GameShortcutResponseDto> games = Collections.singletonList(new GameShortcutResponseDto());
        when(genreService.findGamesByGenresSortedByRating(Collections.singletonList(1L), 10, 0)).thenReturn(games);

        List<GameShortcutResponseDto> result = genreController.getGamesByGenresSortedByRating(Collections.singletonList(1L), 0, 10);

        assertEquals(games, result);
    }
}
