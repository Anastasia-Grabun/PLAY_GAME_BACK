package com.example.playgame.dto.mapper;

import com.example.playgame.dto.purchase.PurchaseRequestDto;
import com.example.playgame.dto.purchase.PurchaseResponseDto;
import com.example.playgame.entity.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseDtoMapper {
    PurchaseDtoMapper INSTANCE = Mappers.getMapper(PurchaseDtoMapper.class);

    PurchaseResponseDto purchaseToPurchaseResposeDto(Purchase purchase);

    Purchase purchaseRequestDtoToPurchase(PurchaseRequestDto purchaseRequestDto);

    List<PurchaseResponseDto> purchasesToPurchaseResponseDtos(List<Purchase> purchases);
}
