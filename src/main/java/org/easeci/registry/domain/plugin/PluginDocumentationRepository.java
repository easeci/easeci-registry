package org.easeci.registry.domain.plugin;

import org.easeci.registry.domain.plugin.dto.DocumentationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PluginDocumentationRepository extends JpaRepository<PluginDocumentationEntity, Long> {

    boolean existsByPluginNameAndPluginVersion(String pluginName, String pluginVersion);

    Optional<PluginDocumentationEntity> findByLastModifiedByAndPluginNameAndPluginVersion(String principalName, String pluginName, String pluginVersion);

    @Query("select new org.easeci.registry.domain.plugin.dto.DocumentationDto(d.documentationTextBytes) from PluginDocumentationEntity d where d.pluginName = :pluginName and d.pluginVersion = :pluginVersion")
    Optional<DocumentationDto> findPluginDocumentationText(String pluginName, String pluginVersion);
}
