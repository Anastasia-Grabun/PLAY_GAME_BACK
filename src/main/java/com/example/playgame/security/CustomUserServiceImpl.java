package com.example.playgame.security;

import com.example.playgame.exception.notfound.CredentialNotFoundException;
import com.example.playgame.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {
    private final CredentialRepository credentialRepository;

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialRepository.findByLogin(username).map(CustomUserDetails::new)
                .orElseThrow(()-> new CredentialNotFoundException(username));
    }
}
