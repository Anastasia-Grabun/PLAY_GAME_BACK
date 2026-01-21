package com.example.playgame.controllers;

import com.example.playgame.dto.credential.*;
import com.example.playgame.service.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credentials")
public class CredentialController {
    private final CredentialService credentialService;

    @GetMapping("/{login}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CredentialResponseDto getCredentialByLogin(@PathVariable String login) {
        return credentialService.getByLogin(login);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CredentialResponseDto> getAllCredentials(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return credentialService.getAll(page, limit);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public void updateCredential(@RequestBody CredentialUpdateDto updatedCredential) {
        credentialService.update(updatedCredential);
    }

    @GetMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CredentialResponseByRoleDto> getCredentialsByRole(
            @PathVariable("id") Long roleId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        return credentialService.getCredentialsWithRole(roleId, page, limit);
    }

    @PostMapping("/add-role")
    @PreAuthorize("hasRole('ADMIN')")
    public void addRoleToCredential(@RequestBody CredentialToAddRoleRequestDto credentialDto) {
        credentialService.addNewRole(credentialDto);;
    }
}

