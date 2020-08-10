package org.easeci.registry.domain.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.easeci.registry.domain.files.RegistryStatus;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PluginDocumentationResponse {
    private RegistryStatus status;
    private String message;
    private Long id;
    private String lastModifiedBy;
    private String pluginName;
    private String pluginVersion;
    private String documentationText;
}
