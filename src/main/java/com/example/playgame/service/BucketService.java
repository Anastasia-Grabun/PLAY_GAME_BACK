package com.example.playgame.service;

import com.example.playgame.dto.bucket.BucketRequestDto;
import com.example.playgame.dto.bucket.BucketResponseDto;

public interface BucketService {
    BucketResponseDto getById(Long id);

    void save(BucketRequestDto bucketDto);

    BucketResponseDto getWishlistByAccountId(Long accountId);

    void addGameToBucket(Long bucketId, Long gameId);

    void removeGameFromBucket(Long bucketId, Long gameId);
}
