package org.easeci.registry.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.easeci.registry.domain.files.RegistryStatus;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponse {
    private RegistryStatus registryStatus;
    private AddPerformerResponse.ValidationError validationError;

    private RegistrationResponse(RegistryStatus registryStatus) {
        this.registryStatus = registryStatus;
        this.validationError = registryStatus.getValidationError();
    }

    public static RegistrationResponse of(RegistryStatus registryStatus) {
        return new RegistrationResponse(registryStatus);
    }
}
