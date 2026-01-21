package com.example.playgame.dto.mapper;

import com.example.playgame.entity.TopUp;
import com.example.playgame.dto.topUp.TopUpResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopUpDtoMapper {
    TopUpDtoMapper INSTANCE = Mappers.getMapper(TopUpDtoMapper.class);

    TopUp topUpDtoToTopUp(TopUpResponseDto topUpDto);

    TopUpResponseDto topUpToTopUpDto(TopUp topUp);

    List<TopUpResponseDto> topUpsToTopUpDtos(List<TopUp> topUp);
}
