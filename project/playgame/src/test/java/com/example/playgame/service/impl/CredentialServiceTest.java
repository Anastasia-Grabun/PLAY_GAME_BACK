package com.example.playgame.service.impl;

import com.example.playgame.dto.credential.*;
import com.example.playgame.dto.mapper.CredentialDtoMapper;
import com.example.playgame.entity.Credential;
import com.example.playgame.entity.Role;
import com.example.playgame.exception.notfound.CredentialNotFoundException;
import com.example.playgame.exception.notfound.RoleNotFoundException;
import com.example.playgame.repository.CredentialRepository;
import com.example.playgame.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CredentialServiceTest {
    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private CredentialDtoMapper credentialDtoMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CredentialServiceImpl credentialService;

    private Credential credential;
    private CredentialResponseDto credentialResponseDto;
    private CredentialRequestToCreateDto credentialRequestDto;
    private CredentialUpdateDto credentialUpdateDto;
    private CredentialToAddRoleRequestDto credentialToAddRoleRequestDto;
    private CredentialResponseByRoleDto credentialResponseByRoleDto;
    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);

        credential = new Credential();
        credential.setLogin("testLogin");

        credentialResponseDto = new CredentialResponseDto();
        credentialResponseDto.setLogin("testLogin");

        credentialRequestDto = new CredentialRequestToCreateDto();
        credentialRequestDto.setLogin("testLogin");

        credentialUpdateDto = new CredentialUpdateDto();
        credentialUpdateDto.setLogin("testLogin");

        credentialToAddRoleRequestDto = new CredentialToAddRoleRequestDto();
        credentialToAddRoleRequestDto.setLogin("testLogin");

        credentialResponseByRoleDto = new CredentialResponseByRoleDto();
        credentialResponseByRoleDto.setLogin("testLogin");
    }

    @Test
    public void testGetByLogin_Success() {
        when(credentialRepository.findByLogin("testLogin")).thenReturn(Optional.of(credential));
        when(credentialDtoMapper.toResponse(credential)).thenReturn(credentialResponseDto);

        CredentialResponseDto result = credentialService.getByLogin("testLogin");

        assertEquals(credentialResponseDto, result);
    }

    @Test
    public void testGetByLogin_NotFound() {
        when(credentialRepository.findByLogin("testLogin")).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class, () -> credentialService.getByLogin("testLogin"));
    }

    @Test
    public void testSave_Success() {
        when(credentialDtoMapper.credentialRequestDtoToCredential(credentialRequestDto)).thenReturn(credential);

        credentialService.save(credentialRequestDto);

        verify(credentialRepository, times(1)).save(credential);
    }

    @Test
    public void testSave_NullCredential() {
        assertThrows(IllegalArgumentException.class, () -> credentialService.save(null));
    }

    @Test
    public void testGetAll_Success() {
        List<Credential> credentials = Collections.singletonList(credential);
        Page<Credential> credentialPage = new PageImpl<>(credentials);
        when(credentialRepository.findAll(any(Pageable.class))).thenReturn(credentialPage);
        when(credentialDtoMapper.credentialsToCredentialResponseDtos(credentials))
                .thenReturn(Collections.singletonList(credentialResponseDto));

        List<CredentialResponseDto> result = credentialService.getAll(0, 10);

        assertEquals(1, result.size());
        assertEquals(credentialResponseDto, result.get(0));
    }

    @Test
    public void testDeleteByLogin_Success() {
        when(credentialRepository.existsByLogin("testLogin")).thenReturn(true);

        credentialService.deleteByLogin("testLogin");

        verify(credentialRepository, times(1)).deleteByLogin("testLogin");
    }

    @Test
    public void testDeleteByLogin_NotFound() {
        when(credentialRepository.existsByLogin("testLogin")).thenReturn(false);

        assertThrows(CredentialNotFoundException.class, () -> credentialService.deleteByLogin("testLogin"));
    }

    @Test
    public void testUpdate_Success() {
        when(credentialDtoMapper.credentialUpdateDtoToCredential(credentialUpdateDto)).thenReturn(credential);

        credentialService.update(credentialUpdateDto);

        verify(credentialRepository, times(1)).save(credential);
    }

    @Test
    public void testUpdate_NullLogin() {
        credentialUpdateDto.setLogin(null);

        assertThrows(IllegalArgumentException.class, () -> credentialService.update(credentialUpdateDto));
    }

    @Test
    public void testAddNewRole_Success() {
        when(credentialRepository.existsByLogin("testLogin")).thenReturn(true);
        when(credentialDtoMapper.credentialRequestToAddRoleDtoToCredential(credentialToAddRoleRequestDto)).thenReturn(credential);

        credentialService.addNewRole(credentialToAddRoleRequestDto);

        verify(credentialRepository, times(1)).save(credential);
    }

    @Test
    public void testAddNewRole_NotFound() {
        when(credentialRepository.existsByLogin("testLogin")).thenReturn(false);

        assertThrows(CredentialNotFoundException.class, () -> credentialService.addNewRole(credentialToAddRoleRequestDto));
    }

    @Test
    public void testGetCredentialsWithRole_Success() {
        List<Credential> credentials = Collections.singletonList(credential);
        Page<Credential> credentialPage = new PageImpl<>(credentials);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(credentialRepository.findByRoles(role, PageRequest.of(0, 10))).thenReturn(credentialPage);
        when(credentialDtoMapper.toResponsesByRole(credentials)).thenReturn(Collections.singletonList(credentialResponseByRoleDto));

        List<CredentialResponseByRoleDto> result = credentialService.getCredentialsWithRole(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(credentialResponseByRoleDto, result.get(0));
    }

    @Test
    public void testGetCredentialsWithRole_RoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> credentialService.getCredentialsWithRole(1L, 0, 10));
    }
}
