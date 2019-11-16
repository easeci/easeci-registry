package org.easeci.registry.domain.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.easeci.registry.domain.files.FileRepresentation;
import org.easeci.registry.domain.files.RegistryStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private RegistryStatus status;
    private FileRepresentation.FileMeta meta;
}
