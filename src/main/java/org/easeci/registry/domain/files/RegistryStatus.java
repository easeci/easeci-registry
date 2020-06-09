package org.easeci.registry.domain.files;

import org.easeci.registry.domain.files.dto.AddPerformerResponse;

public enum RegistryStatus {
    NOT_FOUND {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Cannot find file in specified path");
        }
    },
    INVALID_REJECTED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Saving performer rejected from some reason");
        }
    },
    FOUND {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Found");
        }
    },
    SAVED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Correctly saved");
        }
    },
    SAVE_FAILED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Saving failed. Error with hard drive resource occurred");
        }
    },
    FILE_JUST_EXISTS {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("File just exists");
        }
    },
    DIR_JUST_EXISTS {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Directory just exists");
        }
    },
    CREATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Directory/file correctly created");
        }
    },
    CREATION_FAILED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Directory/file not created");
        }
    },
    JUST_PROCESSING {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("File is just processing");
        }
    };

    public abstract AddPerformerResponse.ValidationError getValidationError();
}
