package com.example.playgame.service.impl;

import com.example.playgame.dto.mapper.PurchaseDtoMapper;
import com.example.playgame.dto.purchase.PurchaseRequestDto;
import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.entity.Purchase;
import com.example.playgame.exception.notfound.PurchaseNotFoundException;
import com.example.playgame.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {
    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseDtoMapper purchaseDtoMapper;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Purchase purchase;
    private PurchaseRequestDto purchaseRequestDto;
    private PurchaseResponseDto purchaseResponseDto;

    @BeforeEach
    public void setUp() {
        purchase = new Purchase();
        purchase.setId(1L);

        purchaseRequestDto = new PurchaseRequestDto();

        purchaseResponseDto = new PurchaseResponseDto();
        purchaseResponseDto.setId(1L);
    }

    @Test
    public void testGetById_Success() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchaseDtoMapper.purchaseToPurchaseResposeDto(purchase)).thenReturn(purchaseResponseDto);

        PurchaseResponseDto result = purchaseService.getById(1L);

        assertEquals(purchaseResponseDto, result);
    }

    @Test
    public void testGetById_NotFound() {
        assertThrows(PurchaseNotFoundException.class, () -> purchaseService.getById(1L));
    }

    @Test
    public void testSave_Success() {
        when(purchaseDtoMapper.purchaseRequestDtoToPurchase(purchaseRequestDto)).thenReturn(purchase);

        purchaseService.save(purchaseRequestDto);

        verify(purchaseRepository, times(1)).save(purchase);
    }

    @Test
    public void testSave_NullPurchase() {
        assertThrows(PurchaseNotFoundException.class, () -> purchaseService.save(null));
    }

    @Test
    public void testGetAll_Success() {
        Page<Purchase> purchasePage = new PageImpl<>(Collections.singletonList(purchase));
        when(purchaseRepository.findAll(PageRequest.of(0, 10))).thenReturn(purchasePage);
        when(purchaseDtoMapper.purchasesToPurchaseResponseDtos(purchasePage.getContent())).thenReturn(Collections.singletonList(purchaseResponseDto));

        List<PurchaseResponseDto> result = purchaseService.getAll(0, 10);

        assertEquals(1, result.size());
        assertEquals(purchaseResponseDto, result.get(0));
    }

    @Test
    public void testGetPurchasesByOwnerId_Success() {
        Page<Purchase> purchasePage = new PageImpl<>(Collections.singletonList(purchase));
        when(purchaseRepository.findByOwnerId(1L, PageRequest.of(0, 10))).thenReturn(purchasePage);
        when(purchaseDtoMapper.purchasesToPurchaseResponseDtos(purchasePage.getContent())).thenReturn(Collections.singletonList(purchaseResponseDto));

        List<PurchaseResponseDto> result = purchaseService.getPurchasesByOwnerId(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(purchaseResponseDto, result.get(0));
    }
}
