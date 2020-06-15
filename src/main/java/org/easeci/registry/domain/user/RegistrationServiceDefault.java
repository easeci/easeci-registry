package org.easeci.registry.domain.user;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.user.dto.RegistrationRequest;
import org.easeci.registry.domain.user.dto.RegistrationResponse;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
class RegistrationServiceDefault implements RegistrationService {
    private RegistryUserRepository registryUserRepository;

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        return map(registryUserRepository.save(RegistryUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .company(request.getCompany())
                .password(createHashedPassword(request.getPassword(), request.getPasswordRepetition()))
                .registrationDate(new Date())
                .website(request.getWebsite())
                .userType(request.getUserType())
                .build()));
    }

    private String createHashedPassword(String password, String passwordRepetition) {
        return null;
    }

    private static RegistrationResponse map(RegistryUser registryUser) {
        return null;
    }
}
