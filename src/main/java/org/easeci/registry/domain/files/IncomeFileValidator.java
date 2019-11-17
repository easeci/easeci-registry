package org.easeci.registry.domain.files;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class IncomeFileValidator {

    static void valid(FileRepresentation request) throws ValidationException {
        throw new ValidationException();
    }

    private static class ValidationException extends RuntimeException {}
}
