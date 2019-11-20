package org.easeci.registry.domain.api.dto;

import lombok.*;
import org.easeci.registry.domain.files.FileRepresentation;
import org.easeci.registry.domain.files.RegistryStatus;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private RegistryStatus status;
    private FileRepresentation.FileMeta meta;
}
