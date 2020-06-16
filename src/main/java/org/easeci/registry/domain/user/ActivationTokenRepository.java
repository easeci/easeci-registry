package org.easeci.registry.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ActivationTokenRepository extends JpaRepository<ActivationTokenEntity, Long> {

    Optional<ActivationTokenEntity> findByActivationToken(String activationToken);
}
