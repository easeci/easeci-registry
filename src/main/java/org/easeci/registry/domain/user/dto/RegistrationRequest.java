package org.easeci.registry.domain.user.dto;

import lombok.Data;
import org.easeci.registry.domain.user.UserType;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.net.URL;

@Data
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
    private URL website;

    @NotNull
    private UserType userType;
}
