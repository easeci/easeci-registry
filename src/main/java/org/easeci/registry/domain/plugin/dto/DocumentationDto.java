package org.easeci.registry.domain.plugin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentationDto {
    private byte[] documentationTextAsBytes;
}
