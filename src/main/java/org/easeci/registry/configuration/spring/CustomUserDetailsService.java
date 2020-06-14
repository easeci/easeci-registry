package org.easeci.registry.configuration.spring;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.user.RegistryUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@AllArgsConstructor
class CustomUserDetailsService implements UserDetailsService {
    private RegistryUserRepository registryUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return registryUserRepository.findByUsername(username).orElseThrow();
    }
}
