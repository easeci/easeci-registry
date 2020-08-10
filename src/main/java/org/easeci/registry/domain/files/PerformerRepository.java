package org.easeci.registry.domain.files;

import org.easeci.registry.domain.files.dto.PerformerVersionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

public interface PerformerRepository extends JpaRepository<PerformerEntity, Long> {

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

    Page<PerformerEntity> findAllByUploaderPrincipalName(String uploaderUsername, Pageable pageable);

    @Query("select new org.easeci.registry.domain.files.dto.PerformerVersionResponse(pve.versionId, pve.performerVersion, " +
            "pve.performerScriptBytes, pve.validated, pve.releaseDate, pve.documentationUrl) " +
            "from PerformerEntity e inner join PerformerVersionEntity pve " +
            "on e.performerId = pve.performer.performerId " +
            "where e.performerName = ?1")
    Set<PerformerVersionResponse> findAllVersionByName(String performerName);

    @Query(value = "select description from performer where performer_name = ?1", nativeQuery = true)
    String findDescription(String performerName);

    @Modifying
    @Transactional
    @Query(value = "update performer set description = :description where performer_name = :performerName", nativeQuery = true)
    void updateDescription(String performerName, String description);

    boolean existsByUploaderPrincipalNameAndPerformerName(String principalName, String pluginName);
}
