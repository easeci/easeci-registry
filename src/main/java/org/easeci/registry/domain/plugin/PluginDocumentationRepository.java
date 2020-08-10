package org.easeci.registry.domain.plugin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PluginDocumentationRepository extends JpaRepository<PluginDocumentationEntity, Long> {

    boolean existsByPluginNameAndPluginVersion(String pluginName, String pluginVersion);

    Optional<PluginDocumentationEntity> findByLastModifiedByAndPluginNameAndPluginVersion(String principalName, String pluginName, String pluginVersion);
}
