package com.example.playgame.controllers;

import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseControllerTest {
    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    private PurchaseResponseDto purchaseResponseDto;

    @BeforeEach
    public void setUp() {
        purchaseResponseDto = new PurchaseResponseDto();
        purchaseResponseDto.setId(1L);
    }

    @Test
    public void testGetPurchaseById_Success() {
        when(purchaseService.getById(1L)).thenReturn(purchaseResponseDto);

        PurchaseResponseDto result = purchaseController.getPurchaseById(1L);

        assertEquals(purchaseResponseDto, result);
    }

    @Test
    public void testGetPurchasesByOwnerId_Success() {
        List<PurchaseResponseDto> purchases = Collections.singletonList(purchaseResponseDto);
        when(purchaseService.getPurchasesByOwnerId(1L, 0, 10)).thenReturn(purchases);

        List<PurchaseResponseDto> result = purchaseController.getPurchasesByOwnerId(1L, 0, 10);

        assertEquals(purchases, result);
    }
}