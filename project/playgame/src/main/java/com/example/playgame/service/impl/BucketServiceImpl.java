package com.example.playgame.service.impl;

import com.example.playgame.dto.bucket.BucketRequestDto;
import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.dto.mapper.BucketDtoMapper;
import com.example.playgame.entity.Bucket;
import com.example.playgame.entity.Game;
import com.example.playgame.entity.enums.BucketType;
import com.example.playgame.exception.BucketNullException;
import com.example.playgame.exception.notfound.BucketNotFoundException;
import com.example.playgame.exception.notfound.GameNotFoundException;
import com.example.playgame.repository.BucketRepository;
import com.example.playgame.repository.GameRepository;
import com.example.playgame.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {
    private final BucketRepository bucketRepository;
    private final GameRepository gameRepository;
    private final BucketDtoMapper bucketDtoMapper;

    @Override
    public BucketResponseDto getById(Long id) {
        if(!bucketRepository.existsById(id)){
            throw new BucketNotFoundException(id);
        }

        Bucket bucket = bucketRepository.getById(id);

        return bucketDtoMapper.bucketToBucketResponseDto(bucket);
    }

    @Override
    public void save(BucketRequestDto bucketDto) {
        if (bucketDto == null) {
            throw new BucketNullException();
        }

        Bucket bucket = bucketDtoMapper.bucketRequestDtoToBucket(bucketDto);
        bucketRepository.save(bucket);
    }

    @Override
    public BucketResponseDto getBucketByAccountId(Long id){
        Bucket bucket = bucketRepository.findBucketByAccount_Id(id)
                .orElseThrow(()-> new BucketNotFoundException(id));

        return bucketDtoMapper.bucketToBucketResponseDto(bucket);
    }

    @Override
    public void addGameToBucket(Long bucketId, Long gameId) {
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(bucketId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));


        bucket.getGames().add(game);
        bucketRepository.save(bucket);
    }

    @Override
    public void removeGameFromBucket(Long bucketId, Long gameId) {
        Bucket bucket = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException(bucketId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        bucket.getGames().remove(game);
        bucketRepository.save(bucket);
    }

    @Override
    public void moveGamesToBuyList(Long accountId, List<Long> gameIds) {
        Bucket wishlist = bucketRepository.findByAccountIdAndBucketType(accountId, BucketType.WISHLIST.toString())
                .orElseThrow(()-> new BucketNotFoundException("Bucket not found"));
        Bucket buylist = bucketRepository.findByAccountIdAndBucketType(accountId, BucketType.BUYLIST.toString())
                .orElseThrow(()-> new BucketNotFoundException("Bucket not found"));;

        for (Long gameId : gameIds) {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("Game not found"));
            wishlist.getGames().remove(game);
            buylist.getGames().add(game);
        }

        bucketRepository.save(wishlist);
        bucketRepository.save(buylist);
    }
}
