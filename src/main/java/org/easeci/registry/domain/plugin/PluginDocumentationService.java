package org.easeci.registry.domain.plugin;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.files.PerformerRepository;
import org.easeci.registry.domain.files.RegistryStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static java.util.Objects.nonNull;
import static org.easeci.registry.domain.files.RegistryStatus.*;

@Service
@AllArgsConstructor
public class PluginDocumentationService {
    private PluginDocumentationRepository pluginDocumentationRepository;
    private PerformerRepository performerRepository;

    public Mono<PluginDocumentationResponse> findDocumentation(Principal principal, String pluginName, String pluginVersion) {
        return pluginDocumentationRepository.findByLastModifiedByAndPluginNameAndPluginVersion(principal.getName(), pluginName, pluginVersion)
                .map(entity -> Mono.just(PluginDocumentationResponse.builder()
                        .status(FOUND)
                        .message(FOUND.getValidationError().getMessage())
                        .id(entity.getId())
                        .lastModifiedBy(entity.getLastModifiedBy())
                        .pluginName(entity.getPluginName())
                        .pluginVersion(entity.getPluginVersion())
                        .documentationText(entity.getDocumentationTextBytes())
                        .build()))
                .orElseGet(() -> Mono.just(PluginDocumentationResponse.builder()
                        .status(NOT_FOUND)
                        .message(NOT_FOUND.getValidationError().getMessage())
                        .pluginName(pluginName)
                        .pluginVersion(pluginVersion)
                        .build()));
    }

    public Mono<RegistryStatus> addDocumentation(Principal principal, PluginDocumentationRequest request) {
        if (pluginDocumentationRepository.existsByPluginNameAndPluginVersion(request.getPluginName(), request.getPluginVersion())) {
            return Mono.just(DOCUMENTATION_JUST_EXISTS);
        }
        if (!performerRepository.existsByUploaderPrincipalNameAndPerformerName(principal.getName(), request.getPluginName())) {
            return Mono.just(PLUGIN_NOT_FOUND);
        }
        PluginDocumentationEntity savedEntity = pluginDocumentationRepository.save(PluginDocumentationEntity.builder()
                .lastModifiedBy(principal.getName())
                .pluginName(request.getPluginName())
                .pluginVersion(request.getPluginVersion())
                .documentationTextBytes(toEncodedBytes(request.getDocumentationText()))
                .build());
        return nonNull(savedEntity.getId()) && savedEntity.getId() != 0 ? Mono.just(DOCUMENTATION_CREATED) : Mono.just(DOCUMENTATION_NOT_CREATED);
    }

    public Mono<RegistryStatus> editDocumentation(Principal principal, PluginDocumentationRequest request) {
        boolean isEditedCorrectly = pluginDocumentationRepository.findByLastModifiedByAndPluginNameAndPluginVersion(
                principal.getName(), request.getPluginName(), request.getPluginVersion())
                .map(pluginDocumentationEntity -> {
                    pluginDocumentationEntity.setLastModifiedBy(principal.getName());
                    pluginDocumentationEntity.setDocumentationTextBytes(toEncodedBytes(request.getDocumentationText()));
                    return pluginDocumentationRepository.save(pluginDocumentationEntity);
                }).isPresent();
        return isEditedCorrectly ? Mono.just(DOCUMENTATION_UPDATED) : Mono.just(PLUGIN_NOT_FOUND);
    }

    private byte[] toEncodedBytes(String text) {
        return text.getBytes();
    }
}
