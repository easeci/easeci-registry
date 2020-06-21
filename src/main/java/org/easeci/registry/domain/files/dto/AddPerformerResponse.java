package org.easeci.registry.domain.files.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

import static java.util.Objects.isNull;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class AddPerformerResponse {
    private boolean isAddedCorrectly;
    private List<ValidationError> validationErrorList;
    private String token;

    public static AddPerformerResponse of(List<ValidationError> validationErrors) {
        return new AddPerformerResponse(validationErrors.isEmpty(), validationErrors, null);
    }

    public static AddPerformerResponse of(boolean isAddedCorrectly, List<ValidationError> validationErrors) {
        return new AddPerformerResponse(isAddedCorrectly, validationErrors, null);
    }

    public static AddPerformerResponse of(List<ValidationError> validationErrors, String token) {
        if (isNull(token)) {
            throw new IllegalArgumentException("Cannot construct object with token as null");
        }
        return new AddPerformerResponse(validationErrors.isEmpty(), validationErrors, token);
    }

    @Data
    @ToString
    @AllArgsConstructor
    public static class ValidationError {
        private String message;
    }
}
