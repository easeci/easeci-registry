package org.easeci.registry.domain.api.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
