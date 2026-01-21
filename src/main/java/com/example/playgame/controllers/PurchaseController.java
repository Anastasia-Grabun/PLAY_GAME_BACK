package com.example.playgame.controllers;

import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PurchaseResponseDto getPurchaseById(@PathVariable Long id) {
        return purchaseService.getById(id);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<PurchaseResponseDto> getPurchasesByOwnerId(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return purchaseService.getPurchasesByOwnerId(ownerId, page, limit);
    }
}
