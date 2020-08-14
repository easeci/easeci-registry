package org.easeci.registry.domain.files.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.easeci.registry.domain.api.dto.BaseResponse;

import java.util.List;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformerUpdateCheckResponse extends BaseResponse {
    private boolean isNewerVersionAvailable;
    private List<PerformerVersionBasic> newerPerformerVersions;
}
