package org.easeci.registry.domain.api.dto;

import lombok.Data;

@Data
public class PluginDescriptionRequest {
    private String description;
    private String performerName;

    public PluginDescriptionRequest(String performerName) {
        this.performerName = performerName;
    }
}
