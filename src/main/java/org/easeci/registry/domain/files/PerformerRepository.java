package org.easeci.registry.domain.files;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface PerformerRepository extends JpaRepository<PerformerEntity, Long> {

    Optional<PerformerEntity> findByPerformerName(String performerName);

    /**
     * This query could tell us if specified version of performer just exists in database.
     * returns 0 (zero) when version not exists,
     * returns > 1 (one or more) when version just exists in database
     * */
    @Query(value = "select count(p.performer_version) from performer_version p " +
            "where performer_id = (select performer_id from performer where performer_name = ?1) " +
            "and p.performer_version = ?2", nativeQuery = true)
    int isVersionExists(String performerName, String performerVersion);

    Page<PerformerEntity> findAll(Pageable pageable);
}
