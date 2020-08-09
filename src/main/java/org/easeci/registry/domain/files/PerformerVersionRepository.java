package org.easeci.registry.domain.files;

import org.easeci.registry.domain.files.dto.PerformerVersionBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerformerVersionRepository extends JpaRepository<PerformerVersionEntity, Long> {

    @Query("select new org.easeci.registry.domain.files.dto.PerformerVersionBasic(e.versionId, e.performerVersion, e.releaseDate) from PerformerVersionEntity e " +
            "where e.performer.performerName = :performerName")
    List<PerformerVersionBasic> findAllByPerformerName(String performerName);
}
