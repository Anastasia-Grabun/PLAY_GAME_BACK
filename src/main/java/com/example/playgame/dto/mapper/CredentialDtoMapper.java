package com.example.playgame.dto.mapper;

import com.example.playgame.dto.credential.CredentialResponseDto;
import com.example.playgame.dto.credential.CredentialRequestToCreateDto;
import com.example.playgame.dto.credential.CredentialResponseByRoleDto;
import com.example.playgame.dto.credential.CredentialToAddRoleRequestDto;
import com.example.playgame.dto.credential.CredentialUpdateDto;
import com.example.playgame.entity.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CredentialDtoMapper {
    CredentialDtoMapper INSTANCE = Mappers.getMapper(CredentialDtoMapper.class);

    CredentialResponseDto toResponse(Credential credential);

    List<CredentialResponseDto> credentialsToCredentialResponseDtos(List<Credential> credentials);

    List<CredentialResponseByRoleDto> toResponsesByRole(List<Credential> credentials);

    Credential credentialUpdateDtoToCredential(CredentialUpdateDto credentialUpdateDto);

    Credential credentialRequestDtoToCredential(CredentialRequestToCreateDto credentialRequestToCreateDto);
    Credential credentialRequestToAddRoleDtoToCredential(CredentialToAddRoleRequestDto credentialRequestToAddRoleDto);
}
