package com.example.playgame.controllers;

import com.example.playgame.dto.transaction.TransactionResponseDto;
import com.example.playgame.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TransactionResponseDto getTransactionById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/account/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<TransactionResponseDto> getAllTransactionsByAccountId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ){
        return transactionService.getAllByAccountId(id, page, limit);
    }
}

