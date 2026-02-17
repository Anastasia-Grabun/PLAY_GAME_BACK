package com.example.playgame.service;

import com.example.playgame.dto.bucket.BucketRequestDto;
import com.example.playgame.dto.bucket.BucketResponseDto;

import java.util.List;

public interface BucketService {
    BucketResponseDto getById(Long id);

    void save(BucketRequestDto bucketDto);

    BucketResponseDto getBucketByAccountId(Long id);

    void addGameToBucket(Long bucketId, Long gameId);

    void removeGameFromBucket(Long bucketId, Long gameId);

    void moveGamesToBuyList(Long accountId, List<Long> gameIds);
}
