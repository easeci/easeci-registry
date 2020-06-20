package org.easeci.registry.domain.files.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor(staticName = "of")
public class AddPerformerResponse {
    private boolean isAddedCorrectly;
    private List<ValidationError> validationErrorList;

    @Data
    @ToString
    @AllArgsConstructor
    public static class ValidationError {
        private String message;
    }
}
