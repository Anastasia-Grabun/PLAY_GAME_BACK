package com.example.playgame.controllers;

import com.example.playgame.dto.credential.*;
import com.example.playgame.dto.role.RoleIdDto;
import com.example.playgame.service.CredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CredentialControllerTest {
    @Mock
    private CredentialService credentialService;

    @InjectMocks
    private CredentialController credentialController;

    private CredentialResponseDto credentialResponseDto;
    private CredentialRequestToCreateDto credentialRequestDto;
    private CredentialUpdateDto credentialUpdateDto;

    @BeforeEach
    public void setUp() {
        credentialResponseDto = new CredentialResponseDto();
        credentialResponseDto.setLogin("testUser ");

        credentialRequestDto = new CredentialRequestToCreateDto();
        credentialRequestDto.setLogin("testUser ");

        credentialUpdateDto = new CredentialUpdateDto();
        credentialUpdateDto.setLogin("testUser ");
    }

    @Test
    public void testGetCredentialByLogin_Success() {
        when(credentialService.getByLogin("testUser ")).thenReturn(credentialResponseDto);

        CredentialResponseDto result = credentialController.getCredentialByLogin("testUser ");

        assertEquals(credentialResponseDto, result);
    }

    @Test
    public void testGetAllCredentials_Success() {
        List<CredentialResponseDto> credentials = Collections.singletonList(credentialResponseDto);
        when(credentialService.getAll(0, 10)).thenReturn(credentials);

        List<CredentialResponseDto> result = credentialController.getAllCredentials(0, 10);

        assertEquals(credentials, result);
    }

    @Test
    public void testUpdateCredential_Success() {
        credentialController.updateCredential(credentialUpdateDto);

        verify(credentialService, times(1)).update(credentialUpdateDto);
    }

    @Test
    public void testGetCredentialsByRole_Success() {
        Long roleId = 1L;
        List<CredentialResponseByRoleDto> credentialsByRole = Collections.singletonList(new CredentialResponseByRoleDto());
        when(credentialService.getCredentialsWithRole(roleId, 0, 10)).thenReturn(credentialsByRole);

        List<CredentialResponseByRoleDto> result = credentialController.getCredentialsByRole(roleId, 0, 10);

        assertEquals(credentialsByRole, result);
    }

    @Test
    public void testAddRoleToCredential_Success() {
        RoleIdDto roleId = new RoleIdDto(1L);

        CredentialToAddRoleRequestDto credentialToAddRoleRequestDto = new CredentialToAddRoleRequestDto();
        credentialToAddRoleRequestDto.setLogin("testUser");
        credentialToAddRoleRequestDto.setRoles(new ArrayList<>());
        credentialToAddRoleRequestDto.getRoles().add(roleId);

        credentialController.addRoleToCredential(credentialToAddRoleRequestDto);

        verify(credentialService, times(1)).addNewRole(credentialToAddRoleRequestDto);
    }
}
