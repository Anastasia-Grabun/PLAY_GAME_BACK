package com.example.playgame.service;

import com.example.playgame.dto.purchase.PurchaseRequestDto;
import com.example.playgame.dto.purchase.PurchaseResponseDto;

import java.util.List;

public interface PurchaseService {
    PurchaseResponseDto getById(Long id);

    void save(PurchaseRequestDto purchaseDto);

    List<PurchaseResponseDto> getAll(int page, int size);

    List<PurchaseResponseDto> getPurchasesByOwnerId(Long accountId, int page, int size);
}
