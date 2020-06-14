package org.easeci.registry.configuration.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("endpoints")
public class NotSecuredEndpointProperties {
    private String[] opened;
}
