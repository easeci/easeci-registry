package org.easeci.registry.domain.api.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.easeci.registry.domain.files.RegistryStatus;

@Getter
@SuperBuilder
public class BaseResponse {
    private RegistryStatus status;
    private String message;
}
