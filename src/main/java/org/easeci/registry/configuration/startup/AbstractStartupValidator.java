package org.easeci.registry.configuration.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
abstract class AbstractStartupValidator implements StartupValidator {

    @Value("${storage.dir}")
    private String storage;

    @Override
    public boolean isStartable() {
        String username = System.getProperty("user.name");
        try {
            Files.createFile(Path.of(storage + "/controlfile"));
        } catch (AccessDeniedException e) {
            log.error("User " + username + " has no rights to resources in: '" + storage + "'.\n" +
                    "Please grant privileges or run with another user with read/write rights");
        } catch (FileAlreadyExistsException e) {
            log.info("Access to resources in " + storage + " correctly assigned");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Files.exists(Path.of(storage));
    }
}
