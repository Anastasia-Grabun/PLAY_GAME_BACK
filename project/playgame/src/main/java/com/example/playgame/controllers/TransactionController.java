package com.example.playgame.controllers;

import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final AuthService authService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TransactionResponseDto getTransactionById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<TransactionResponseDto> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return transactionService.getAllByAccountId(accountId, page, limit);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TransactionResponseDto> getTransactionsByAccountId(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return transactionService.getAllByAccountId(accountId, page, limit);
    }
}
