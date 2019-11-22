package org.easeci.registry.domain.files.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformerVersionResponse {
    private Long versionId;
    private String performerVersion;
    private long performerScriptBytes;
    private Boolean validated;
    private LocalDateTime releaseDate;
    private String documentationUrl;
}
