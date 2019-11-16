package org.easeci.registry.configuration.startup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
class StartupValidatorImpl extends AbstractStartupValidator {

    @Value("${storage.dir}")
    private String storage;

    @PostConstruct
    void checkEnvironment() throws StartupException {
        if (!super.isStartable()) {
            throw new StartupException(
                            "Something went wrong on EaseCI Registry startup.\n" +
                            "This exception is caused by not properly environment prepared." +
                            "For example it is few possibilities, check if your instance is running " +
                            "by user with correct rights to read/write files to directory '" + storage +
                            "'\nOr maybe directory '" + storage + "' not exist at all?"
            );
        }
        else log.info("Successfully prepared environment -> ready to startup");
    }

    @AllArgsConstructor
    private static class StartupException extends Throwable {
        private String message;

        @Override
        public String getMessage() {
            return this.message;
        }
    }
}
