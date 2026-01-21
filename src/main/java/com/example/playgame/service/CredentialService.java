package com.example.playgame.service;

import com.example.playgame.dto.credential.*;

import java.util.List;

public interface CredentialService {
    CredentialResponseDto getByLogin(String login);

    void deleteByLogin(String login);

    void save(CredentialRequestToCreateDto credentialDto);

    void addNewRole(CredentialToAddRoleRequestDto credentialDto);

    List<CredentialResponseByRoleDto> getCredentialsWithRole(Long roleId, int page, int size);

    List<CredentialResponseDto> getAll(int page, int size);

    void update(CredentialUpdateDto updatedCredential);
}
