package org.easeci.registry.domain.user;

import org.easeci.registry.domain.user.dto.RegistrationRequest;
import org.easeci.registry.domain.user.dto.RegistrationResponse;

public interface RegistrationService {

    RegistrationResponse register(RegistrationRequest registrationRequest);
}
