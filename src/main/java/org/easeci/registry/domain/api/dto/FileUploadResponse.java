package org.easeci.registry.domain.api.dto;

import lombok.*;
import org.easeci.registry.domain.files.FileRepresentation;
import org.easeci.registry.domain.files.RegistryStatus;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private boolean isUploaded;
    private RegistryStatus status;
    private FileRepresentation.FileMeta meta;
    private List<AddPerformerResponse.ValidationError> validationErrorList;
}
