package org.easeci.registry.domain.files;

public enum RegistryStatus {
    NOT_FOUND,
    INVALID_REJECTED,
    FOUND,
    SAVED,
    SAVE_FAILED,
    FILE_JUST_EXISTS,
    DIR_JUST_EXISTS,
    CREATED,
    CREATION_FAILED,
    JUST_PROCESSING
}
