package org.easeci.registry.domain.files;

import org.easeci.registry.domain.files.dto.AddPerformerResponse;

public enum RegistryStatus {
    NOT_FOUND {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Cannot find file in specified path");
        }
    },
    PLUGIN_NOT_FOUND {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Cannot find plugin");
        }
    },
    PLUGIN_MORE_THAN_ONE_RESULTS {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Cannot find one plugin because more than one the same versions exists");
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
    },

    //    plugin documentation
    DOCUMENTATION_JUST_EXISTS {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Cannot add documentation because it just exist");
        }
    },
    DOCUMENTATION_CREATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Documentation for plugin version added correctly");
        }
    },
    DOCUMENTATION_NOT_CREATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Documentation for plugin version not created for unrecognized reason");
        }
    },
    DOCUMENTATION_UPDATED {
        @Override
        public AddPerformerResponse.ValidationError getValidationError() {
            return new AddPerformerResponse.ValidationError("Documentation for plugin version updated correctly");
        }
    };

    public abstract AddPerformerResponse.ValidationError getValidationError();
}
