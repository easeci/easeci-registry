package org.easeci.registry.domain.user.dto;

import lombok.Data;
import lombok.ToString;
import org.easeci.registry.domain.user.UserType;
import org.hibernate.validator.constraints.Length;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@ToString
public class RegistrationRequest {

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]{5,30}")
    private String username;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @Pattern(regexp = "[a-zA-Z0-9\\s]{5,30}")
    private String company;

    @NotNull
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-_!@#$%^&*()=+<>?/';]{8,40}")
    private String password;

    @NotNull
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-_!@#$%^&*()=+<>?/';]{8,40}")
    private String passwordRepetition;

    @Length(max = 255)
    private String website;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserType userType;
}
