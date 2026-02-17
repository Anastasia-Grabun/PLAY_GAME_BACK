package com.example.playgame.controllers;

import com.example.playgame.dto.topUp.TopUpResponseDto;
import com.example.playgame.service.TopUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/topups")
@RequiredArgsConstructor
public class TopUpController {
    private final TopUpService topUpService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public TopUpResponseDto getTopUpById(@PathVariable Long id) {
        return topUpService.getTopUpById(id);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<TopUpResponseDto> getTopUpsByAccountId(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return topUpService.getTopUpsByAccountId(accountId, page, limit);
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public void deposit(@RequestParam Long accountId, @RequestParam BigDecimal amount) {
        topUpService.deposit(accountId, amount);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{senderAccountId}/transfer/{receiverAccountId}")
    public void transferFunds(
            @PathVariable Long senderAccountId,
            @PathVariable Long receiverAccountId,
            @RequestParam BigDecimal amount) {
        topUpService.transferFunds(senderAccountId, receiverAccountId, amount);
    }
}

