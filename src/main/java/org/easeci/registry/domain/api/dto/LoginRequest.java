package org.easeci.registry.domain.api.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@ToString
public class LoginRequest {

    @NotNull
    @NotBlank
    @Length(min = 3, max = 50)
    private String username;

    @NotNull
    @NotBlank
    @Length(min = 3, max = 50)
    private String password;
}
