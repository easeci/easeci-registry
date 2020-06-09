package org.easeci.registry.domain.files.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class AddPerformerResponse {
    private boolean isAddedCorrectly;
    private List<ValidationError> validationErrorList;

    @Data
    @AllArgsConstructor
    public static class ValidationError {
        private String message;
    }
}
