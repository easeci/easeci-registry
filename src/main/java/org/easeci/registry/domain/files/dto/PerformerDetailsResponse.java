package org.easeci.registry.domain.files.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.easeci.registry.domain.api.dto.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformerDetailsResponse extends BaseResponse {
    private Long performerId;
    private String authorFullname;
    private String authorEmail;
    private String company;
    private LocalDateTime creationDate;
    private String performerName;
    private String description;
    private boolean isNewerVersionAvailable;
//    TODO add here documentation from PluginDocumentationEntity.class
    private List<PerformerVersionBasic> newerPerformerVersions;
    private Set<PerformerVersionResponse> performerVersions;
}
