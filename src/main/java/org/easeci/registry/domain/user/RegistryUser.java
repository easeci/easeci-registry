package org.easeci.registry.domain.user;

import lombok.Data;

@Data
public class RegistryUser {
    private String username;
    private String email;
    private String password;
}
