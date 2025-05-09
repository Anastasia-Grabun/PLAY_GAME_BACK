package com.example.playgame.service.impl;

import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.BucketNotFoundException;
import com.example.playgame.exception.notfound.DevelopersGamesNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final GameDtoMapper gameDtoMapper;
    private final BucketRepository bucketRepository;
    private final AccountRepository accountRepository;

    @Override
    public GameResponseDto getById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        return gameDtoMapper.gameToGameResponseDto(game);
    }

    @Override
    public void save(GameRequestDto gameDto) {
        Game newGame = gameDtoMapper.gameRequestDtoTOGame(gameDto);

        gameRepository.save(newGame);
    }

    @Override
    public List<GameShortcutResponseDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findAll(pageable);

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    public void deleteById(Long id) {
        if(!gameRepository.existsById(id)){
            throw new GameNotFoundException(id);
        }

        gameRepository.deleteById(id);
    }

    @Override
    public void update(GameUpdateDto updatedGameDto) {
        if (!gameRepository.existsById(updatedGameDto.getId())) {
            throw new GameNotFoundException(updatedGameDto.getId());
        }

        Game updatedGame = gameDtoMapper.gameUpdateDtoToGame(updatedGameDto);
        gameRepository.save(updatedGame);
    }

    @Override
    public List<GameShortcutResponseDto> findGamesByDeveloper(Long developerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByDeveloperId(developerId, pageable);

        if (gamesPage.isEmpty()) {
            throw new DevelopersGamesNotFoundException(developerId);
        }

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    public GameShortcutResponseDto findGameByName(String name) {
        Game game = gameRepository.findByName(name)
                .orElseThrow(() -> new GameNotFoundException("Game with name " + name + " not found"));
        return gameDtoMapper.gameToGameShortcutResponseDto(game);
    }

    @Override
    public List<GameShortcutResponseDto> sortGamesByRating(boolean ascending, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Game> gamesPage;

        if (ascending) {
            gamesPage = gameRepository.findAllByOrderByRatingAsc(pageable);
        } else {
            gamesPage = gameRepository.findAllByOrderByRatingDesc(pageable);
        }

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    public List<GameShortcutResponseDto> sortGamesByPrice(boolean ascending, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage;

        if (ascending) {
            gamesPage = gameRepository.findAllByOrderByPriceAsc(pageable);
        } else {
            gamesPage = gameRepository.findAllByOrderByPriceDesc(pageable);
        }

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    public List<GameShortcutResponseDto> findGamesByGenre(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByGenre(genreId, pageable);

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    public List<GameShortcutResponseDto> findGamesInPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByPriceBetween(minPrice, maxPrice, pageable);

        return gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
    }

    @Override
    @Transactional
    public void addGameToBucket(Long bucketId, Long gameId) {
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(bucketId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        bucket.getGames().add(game);
        bucketRepository.save(bucket);
    }

    @Override
    @Transactional
    public void removeGameFromBucket(Long bucketId, Long gameId) {
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(bucketId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        bucket.getGames().remove(game);
        bucketRepository.save(bucket);
    }

    @Override
    @Transactional
    public void addRating(Long gameId, Long accountId, BigDecimal rating) {
        if (!gameRepository.existsById(gameId)) {
            throw new GameNotFoundException(gameId);
        }

        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        if (!gameRepository.existsRatingByAccountAndGame(accountId, gameId)) {
            throw new IllegalArgumentException("Your rating already exists and you cannot change it.");
        }

        gameRepository.addRatingIfNotExists(accountId, gameId, rating);
    }
}
