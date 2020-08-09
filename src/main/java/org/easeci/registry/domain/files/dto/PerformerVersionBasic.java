package org.easeci.registry.domain.files.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PerformerVersionBasic {
    @JsonIgnore private Long versionId;
    private String performerVersion;
    private LocalDateTime releaseDate;
}
