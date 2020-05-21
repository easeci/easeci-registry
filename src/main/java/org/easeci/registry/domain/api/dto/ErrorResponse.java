package org.easeci.registry.domain.api.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private Class<?> exception;
    private String message;

    public ErrorResponse(Exception exception) {
        this.exception = exception.getClass();
        this.message = exception.getMessage();
    }
}
