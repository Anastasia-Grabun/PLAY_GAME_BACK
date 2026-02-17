package com.example.playgame.controllers;

import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buckets")
public class BucketController {
    private final BucketService bucketService;
    private final AuthService authService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public BucketResponseDto getBucketById(@PathVariable Long id) {
        return bucketService.getById(id);
    }

    @GetMapping("/me/wishlist")
    @PreAuthorize("hasRole('USER')")
    public BucketResponseDto getMyWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return bucketService.getWishlistByAccountId(accountId);
    }

    @GetMapping("/me/buylist")
    @PreAuthorize("hasRole('USER')")
    public BucketResponseDto getMyBuylist(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return bucketService.getBuylistByAccountId(accountId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{bucketId}/games/{gameId}")
    public void addGameToBucket(@PathVariable Long bucketId, @PathVariable Long gameId) {
        bucketService.addGameToBucket(bucketId, gameId);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{bucketId}/games/{gameId}")
    public void removeGameFromBucket(@PathVariable Long bucketId, @PathVariable Long gameId) {
        bucketService.removeGameFromBucket(bucketId, gameId);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/me/move-to-buylist")
    public void moveGamesToBuyList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<Long> gameIds) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        bucketService.moveGamesToBuyList(accountId, gameIds);
    }
}
