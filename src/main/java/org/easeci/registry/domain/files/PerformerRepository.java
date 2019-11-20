package org.easeci.registry.domain.files;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PerformerRepository extends JpaRepository<PerformerEntity, Long> {

    Optional<PerformerEntity> findByPerformerName(String performerName);
}
