package com.example.playgame.controllers;

import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
public class BucketController {
    private final BucketService bucketService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BucketResponseDto getBucketById(@PathVariable Long id) {
        return bucketService.getById(id);
    }

    @GetMapping("/accounts/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BucketResponseDto getBucketsByAccountId(@PathVariable Long id){
        return bucketService.getBucketByAccountId(id);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/bucket/{bucketId}/game/{gameId}")
    public void addGameToBucket(@PathVariable Long bucketId, @PathVariable Long gameId){
        bucketService.addGameToBucket(bucketId, gameId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/bucket/{bucketId}/game/{gameId}")
    public void removeGameFromBucket(@PathVariable Long bucketId, @PathVariable Long gameId){
        bucketService.removeGameFromBucket(bucketId, gameId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/accounts/{accountId}/games/move-to-buylist")
    public void moveGamesToBuyList(@PathVariable Long accountId, @RequestBody List<Long> gameIds) {
        bucketService.moveGamesToBuyList(accountId, gameIds);
    }
}

