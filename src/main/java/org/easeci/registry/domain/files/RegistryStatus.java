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
    },

//  Statuses for user registration
    USER_CREATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("User created with success");
        }
    },
    USER_ACTIVATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("User activated with success");
        }
    },
    PASSWORDS_NOT_EQUAL {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Password and password repetition must be equal");
        }
    },
    USERNAME_OR_EMAIL_EXISTS {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Username or email address just exists in EaseCI registry");
        }
    },
    REGISTRATION_ERROR {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Server error occurred while registration process");
        }
    };

    public abstract AddPerformerResponse.ValidationError getValidationError();
}
