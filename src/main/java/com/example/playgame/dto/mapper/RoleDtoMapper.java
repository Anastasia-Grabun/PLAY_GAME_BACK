package com.example.playgame.dto.mapper;

import com.example.playgame.dto.role.RoleRequestDto;
import com.example.playgame.dto.role.RoleResponseDto;
import com.example.playgame.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleDtoMapper {
    RoleDtoMapper INSTANCE = Mappers.getMapper(RoleDtoMapper.class);

    Role roleRequestDtoToRole(RoleRequestDto roleRequestDto);

    RoleResponseDto roleToRoleResponseDto(Role role);

    List<RoleResponseDto> rolesToRoleResponseDtos(List<Role> role);
}
