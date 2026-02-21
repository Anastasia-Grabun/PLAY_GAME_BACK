package com.example.playgame.controllers;

import com.example.playgame.service.AuthService;
import com.example.playgame.service.BuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout")
public class BuyController {

    private final BuyService buyService;
    private final AuthService authService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkout(@AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        buyService.purchaseGames(accountId);
    }
}


