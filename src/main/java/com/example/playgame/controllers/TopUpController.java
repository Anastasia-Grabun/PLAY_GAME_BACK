package com.example.playgame.controllers;

import com.example.playgame.dto.topUp.TopUpResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.TopUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/topups")
@RequiredArgsConstructor
public class TopUpController {
    private final TopUpService topUpService;
    private final AuthService authService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TopUpResponseDto getTopUpById(@PathVariable Long id) {
        return topUpService.getTopUpById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<TopUpResponseDto> getMyTopUps(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return topUpService.getTopUpsByAccountId(accountId, page, limit);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopUpResponseDto> getTopUpsByAccountId(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return topUpService.getTopUpsByAccountId(accountId, page, limit);
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam BigDecimal amount) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        topUpService.deposit(accountId, amount);
    }

    @PutMapping("/me/transfer/{receiverAccountId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferFunds(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long receiverAccountId,
            @RequestParam BigDecimal amount) {
        Long senderAccountId = authService.getAccountIdByLogin(userDetails.getUsername());
        topUpService.transferFunds(senderAccountId, receiverAccountId, amount);
    }
}
