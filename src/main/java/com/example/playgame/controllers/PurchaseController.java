package com.example.playgame.controllers;

import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.service.AuthService;
import com.example.playgame.service.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Purchases", description = "Покупки игр: просмотр по id, свои покупки (/me), по владельцу (ADMIN)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final AuthService authService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PurchaseResponseDto getPurchaseById(@PathVariable Long id) {
        return purchaseService.getById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public List<PurchaseResponseDto> getMyPurchases(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long accountId = authService.getAccountIdByLogin(userDetails.getUsername());
        return purchaseService.getPurchasesByOwnerId(accountId, page, limit);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PurchaseResponseDto> getPurchasesByOwnerId(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return purchaseService.getPurchasesByOwnerId(ownerId, page, limit);
    }
}
