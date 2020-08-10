package org.easeci.registry.domain.plugin;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static java.util.Optional.ofNullable;

@Data
@NoArgsConstructor
public class PluginDocumentationRequest {

    @NotNull
    private String pluginName;

    @NotNull
    private String pluginVersion;

    @NotNull
    private String documentationText;

    public PluginDocumentationRequest(String pluginName, String pluginVersion, String documentationText) {
        this.documentationText = ofNullable(documentationText).orElseGet(Utils::docTip);
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }
}
