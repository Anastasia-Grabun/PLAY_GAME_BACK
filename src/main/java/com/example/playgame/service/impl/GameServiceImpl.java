package com.example.playgame.service.impl;

import com.example.playgame.dto.account.AccountWithIdAndUsernameDto;
import com.example.playgame.dto.game.GameRequestDto;
import com.example.playgame.dto.game.GameResponseDto;
import com.example.playgame.dto.game.GameShortcutResponseDto;
import com.example.playgame.dto.game.GameUpdateDto;
import com.example.playgame.dto.mapper.GameDtoMapper;
import com.example.playgame.dto.mapper.GenreDtoMapper;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.Genre;
import com.example.playgame.exception.notfound.AccountNotFoundException;
import com.example.playgame.exception.notfound.BucketNotFoundException;
import com.example.playgame.exception.notfound.DevelopersGamesNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.AccountRepository;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.service.GameService;
import com.example.playgame.util.ImageUrlResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final GameDtoMapper gameDtoMapper;
    private final BucketRepository bucketRepository;
    private final AccountRepository accountRepository;
    private final GenreDtoMapper genreDtoMapper;
    private final ImageUrlResolver imageUrlResolver;

    @Override
    public GameResponseDto getById(Long id) {
        Game game = gameRepository.findByIdWithGenresAndDeveloper(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        GameResponseDto dto = gameDtoMapper.gameToGameResponseDto(game);
        resolveCoverUrl(dto);
        return dto;
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
        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
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

        Game existingGame = gameRepository.findById(updatedGameDto.getId())
                .orElseThrow(() -> new GameNotFoundException(updatedGameDto.getId()));

        if (updatedGameDto.getName() != null) {
            existingGame.setName(updatedGameDto.getName());
        }

        if (updatedGameDto.getDescription() != null) {
            existingGame.setDescription(updatedGameDto.getDescription());
        }

        if (updatedGameDto.getPrice() != null) {
            existingGame.setPrice(updatedGameDto.getPrice());
        }

        if (updatedGameDto.getGenres() != null) {
            List<Genre> genres = updatedGameDto.getGenres().stream()
                    .map(genreDtoMapper::genreRequestDtoToGenre)
                    .collect(Collectors.toList());
            existingGame.setGenres(genres);
        }

        gameRepository.save(existingGame);
    }


    @Override
    public List<GameShortcutResponseDto> findGamesByDeveloper(Long developerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByDeveloperId(developerId, pageable);

        if (gamesPage.isEmpty()) {
            throw new DevelopersGamesNotFoundException(developerId);
        }

        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
    }

    @Override
    public GameShortcutResponseDto findGameByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Укажите имя для поиска");
        }
        Game game = gameRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new GameNotFoundException("Игра не найдена"));
        GameShortcutResponseDto dto = gameDtoMapper.gameToGameShortcutResponseDto(game);
        resolveCoverUrl(dto);
        return dto;
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

        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
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

        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
    }

    @Override
    public List<GameShortcutResponseDto> findGamesByGenre(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByGenre(genreId, pageable);

        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
    }

    @Override
    public List<GameShortcutResponseDto> findGamesInPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Game> gamesPage = gameRepository.findByPriceBetween(minPrice, maxPrice, pageable);

        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
    }

    @Override
    @Transactional
    public void addGameToBucket(Long bucketId, Long gameId) {
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(bucketId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (bucket.getGames().stream().noneMatch(g -> g.getId().equals(game.getId()))) {
            bucket.getGames().add(game);
            bucketRepository.save(bucket);
        }
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
    public List<AccountWithIdAndUsernameDto> getDevelopers() {
        return accountRepository.findDevelopersWithGames().stream()
                .map(a -> new AccountWithIdAndUsernameDto(a.getId(), a.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GameShortcutResponseDto> getNewReleases(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Game> gamesPage = gameRepository.findAllByOrderByDateDesc(pageable);
        List<GameShortcutResponseDto> dtos = gameDtoMapper.gamesToGameShortcutDtos(gamesPage.getContent());
        dtos.forEach(this::resolveCoverUrl);
        return dtos;
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

        if (gameRepository.existsRatingByAccountAndGame(accountId, gameId)) {
            throw new IllegalArgumentException("Рейтинг уже существует и вы не можете  его изменить");
        }

        if (rating == null) {
            throw new IllegalArgumentException("Рейтинг не может быть null");
        }

        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new IllegalArgumentException("Нельзя поставить больше 5 или меньше 0. Допустимый диапазон: 0–5.");
        }

        if (rating.scale() > 0 && rating.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Рейтинг должен быть целым числом от 0 до 5");
        }

        gameRepository.addRatingIfNotExists(accountId, gameId, rating);
    }

    @Override
    public boolean hasRated(Long gameId, Long accountId) {
        return gameRepository.existsRatingByAccountAndGame(accountId, gameId);
    }


    private void resolveCoverUrl(GameResponseDto dto) {
        if (dto != null) dto.setCoverImageUrl(imageUrlResolver.resolve(dto.getCoverImageUrl()));
    }

    private void resolveCoverUrl(GameShortcutResponseDto dto) {
        if (dto != null) dto.setCoverImageUrl(imageUrlResolver.resolve(dto.getCoverImageUrl()));
    }
}
