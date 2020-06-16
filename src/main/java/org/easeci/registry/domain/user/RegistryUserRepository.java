package org.easeci.registry.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistryUserRepository extends JpaRepository<RegistryUser, Long> {

    Optional<RegistryUser> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);
}
