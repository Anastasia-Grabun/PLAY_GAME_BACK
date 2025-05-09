package com.example.playgame.service.impl;

import com.example.playgame.dto.credential.*;
import com.example.playgame.dto.mapper.CredentialDtoMapper;
import com.example.playgame.entity.Credential;
import com.example.playgame.entity.Role;
import com.example.playgame.exception.notfound.CredentialNotFoundException;
import com.example.playgame.exception.notfound.RoleNotFoundException;
import com.example.playgame.repository.CredentialRepository;
import com.example.playgame.repository.RoleRepository;
import com.example.playgame.service.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
    private final CredentialRepository credentialRepository;
    private final CredentialDtoMapper credentialDtoMapper;
    private final RoleRepository roleRepository;

    @Override
    public CredentialResponseDto getByLogin(String login) {
        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new CredentialNotFoundException(login));

        return credentialDtoMapper.toResponse(credential);
    }

    @Override
    public void save(CredentialRequestToCreateDto credentialDto) {
        if (credentialDto == null) {
            throw new IllegalArgumentException("Credential cannot be null");
        }

        Credential credential = credentialDtoMapper.credentialRequestDtoToCredential(credentialDto);

        credentialRepository.save(credential);
    }

    @Override
    public List<CredentialResponseDto> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Credential> credentialPage = credentialRepository.findAll(pageable);

        return credentialDtoMapper.credentialsToCredentialResponseDtos(credentialPage.getContent());
    }

    @Override
    public void deleteByLogin(String login) {
        if(!credentialRepository.existsByLogin(login)){
            throw new CredentialNotFoundException(login);
        }

        credentialRepository.deleteByLogin(login);
    }

    @Override
    public void update(CredentialUpdateDto updatedCredential) {
        if (updatedCredential.getLogin() == null) {
            throw new IllegalArgumentException("Credential's login cannot be null");
        }

        Credential credential = credentialDtoMapper.credentialUpdateDtoToCredential(updatedCredential);
        credentialRepository.save(credential);
    }

    @Override
    public void addNewRole(CredentialToAddRoleRequestDto credentialDto){
        if(!credentialRepository.existsByLogin(credentialDto.getLogin())){
            throw new CredentialNotFoundException(credentialDto.getLogin());
        }

        Credential credential = credentialDtoMapper.credentialRequestToAddRoleDtoToCredential(credentialDto);
        credentialRepository.save(credential);
    }

    @Override
    public List<CredentialResponseByRoleDto> getCredentialsWithRole(Long roleId, int page, int size){
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new RoleNotFoundException(roleId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Credential> credentialsPage = credentialRepository.findByRoles(role, pageable);

        return credentialDtoMapper.toResponsesByRole(credentialsPage.getContent());
    }
}