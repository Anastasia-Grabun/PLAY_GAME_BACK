package com.example.playgame.service.impl;

import com.example.playgame.dto.mapper.PurchaseDtoMapper;
import com.example.playgame.dto.purchase.PurchaseRequestDto;
import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.entity.Purchase;
import com.example.playgame.exception.notfound.PurchaseNotFoundException;
import com.example.playgame.repository.PurchaseRepository;
import com.example.playgame.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseDtoMapper purchaseDtoMapper;

    @Override
    public PurchaseResponseDto getById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(()-> new PurchaseNotFoundException(id));

        return purchaseDtoMapper.purchaseToPurchaseResposeDto(purchase);
    }

    @Override
    public void save(PurchaseRequestDto purchaseDto) {
        if (purchaseDto == null) {
            throw new PurchaseNotFoundException("Purchase cannot be null");
        }

        Purchase purchase = purchaseDtoMapper.purchaseRequestDtoToPurchase(purchaseDto);
        purchaseRepository.save(purchase);
    }

    @Override
    public List<PurchaseResponseDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Purchase> purchases = purchaseRepository.findAll(pageable);

        return purchaseDtoMapper.purchasesToPurchaseResponseDtos(purchases.getContent());
    }

    @Override
    public List<PurchaseResponseDto> getPurchasesByOwnerId(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Purchase> purchases =  purchaseRepository.findByOwnerId(accountId, pageable);

        return purchaseDtoMapper.purchasesToPurchaseResponseDtos(purchases.getContent());
    }
}
