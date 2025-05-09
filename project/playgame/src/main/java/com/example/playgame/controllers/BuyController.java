package com.example.playgame.controllers;

import com.example.playgame.service.BuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/buying")
public class BuyController {
    private final BuyService buyService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/account/{accountId}/owner/{ownerId}")
    public void buyGame(@PathVariable Long accountId, @PathVariable Long ownerId) {
        buyService.purchaseGames(accountId, ownerId);
    }
}

