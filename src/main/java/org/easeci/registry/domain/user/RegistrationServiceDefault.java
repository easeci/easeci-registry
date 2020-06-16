package org.easeci.registry.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.user.dto.RegistrationRequest;
import org.easeci.registry.domain.user.dto.RegistrationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.nonNull;
import static org.easeci.registry.domain.files.RegistryStatus.*;

@Slf4j
@Service
class RegistrationServiceDefault implements RegistrationService {
    private RegistryUserRepository registryUserRepository;
    private ActivationTokenRepository activationTokenRepository;
    private PasswordEncoder passwordEncoder;

    @Value("${activation.url}")
    private String activationUrl;

    RegistrationServiceDefault(RegistryUserRepository registryUserRepository,
                               ActivationTokenRepository activationTokenRepository, PasswordEncoder passwordEncoder) {
        this.registryUserRepository = registryUserRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        return validationChain(request)
                .orElseGet(() -> onSuccess(registryUserRepository.save(RegistryUser.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .company(request.getCompany())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .registrationDate(new Date())
                        .website(request.getWebsite())
                        .userType(request.getUserType())
                        .build())));
    }

    @Override
    public RegistrationResponse activate(String activationToken) {
        return activationTokenRepository.findByActivationToken(new String(Base64.getDecoder().decode(activationToken)))
                .map(activationTokenEntity -> {
                    activationTokenRepository.delete(activationTokenEntity);
                    return registryUserRepository.findById(activationTokenEntity.getRegistryUserId())
                            .map(registryUser -> {
                                registryUser.setAccountNotExpired(true);
                                registryUser.setAccountNonLocked(true);
                                registryUser.setCredentialsNonExpired(true);
                                registryUser.setEnabled(true);
                                return registryUserRepository.save(registryUser);
                            });
                })
                .filter(registryUser -> registryUser.orElseThrow().isEnabled())
                .map(registryUser -> RegistrationResponse.of(USER_ACTIVATED))
                .orElseGet(() -> RegistrationResponse.of(REGISTRATION_ERROR));
    }

    private Optional<RegistrationResponse> validationChain(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getPasswordRepetition())) {
            return Optional.of(RegistrationResponse.of(PASSWORDS_NOT_EQUAL));
        }
        if (registryUserRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            return Optional.of(RegistrationResponse.of(USERNAME_OR_EMAIL_EXISTS));
        }
        return Optional.empty();
    }

    private RegistrationResponse onSuccess(RegistryUser registryUser) {
        log.info("{} correctly registered in service", registryUser.getUsername());
        ActivationTokenEntity activationTokenEntity = prepare(registryUser);
        CompletableFuture.runAsync(() -> sendMail(activationTokenEntity, registryUser.getEmail()));
        return nonNull(registryUser.getId()) ? RegistrationResponse.of(USER_CREATED) : RegistrationResponse.of(REGISTRATION_ERROR);
    }

    private ActivationTokenEntity prepare(RegistryUser registryUser) {
        return activationTokenRepository.save(ActivationTokenEntity.builder()
                .registryUserId(registryUser.getId())
                .activationToken(passwordEncoder.encode(registryUser.getUsername().concat(registryUser.getEmail())))
                .build());
    }

    private String prepareActivationLink(ActivationTokenEntity activationTokenEntity) {
        return activationUrl.concat(new String(Base64.getEncoder().encode(activationTokenEntity.getActivationToken().getBytes())));
    }

    private void sendMail(ActivationTokenEntity activationTokenEntity, String email) {
        System.out.println("Activation link: " + prepareActivationLink(activationTokenEntity));
    }
}
