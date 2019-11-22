package org.easeci.registry.domain.files.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PerformerResponse {
    private Long performerId;
    private String authorFullname;
    private String authorEmail;
    private String company;
    private LocalDateTime creationDate;
    private String performerName;
    private String description;
    private Set<PerformerVersionResponse> performerVersions;
}

