package com.example.playgame.dto.mapper;

import com.example.playgame.dto.bucket.BucketRequestDto;
import com.example.playgame.dto.bucket.BucketResponseDto;
import com.example.playgame.entity.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BucketDtoMapper {
    BucketDtoMapper INSTANCE = Mappers.getMapper(BucketDtoMapper.class);

    BucketResponseDto bucketToBucketResponseDto(Bucket bucket);

    Bucket bucketRequestDtoToBucket(BucketRequestDto bucketRequestDto);

    List<BucketResponseDto> bucketsToBucketResponseDto(List<Bucket> buckets);
}
