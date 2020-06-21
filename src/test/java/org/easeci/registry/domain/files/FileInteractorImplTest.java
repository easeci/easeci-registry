package org.easeci.registry.domain.files;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.easeci.registry.utils.CommonUtils.factorizeFileRepresentation;
import static org.easeci.registry.utils.CommonUtils.factorizeFileRepresentationNextVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({ "test" })
class FileInteractorImplTest {
    private static String DIRECTORY_PATH = "/tmp/ease/registry";

    @Autowired
    private FileInteractor fileInteractor;

    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(Paths.get(DIRECTORY_PATH));
    }

    @Test
    @DisplayName("Should correctly persist Performer Script if name not exists")
    void persistNameNotExist() {
        FileRepresentation fileRepresentation = factorizeFileRepresentation();

        RegistryStatus status = fileInteractor.persist(fileRepresentation);

        assertEquals(RegistryStatus.SAVED, status);
    }

    @Test
    @DisplayName("Should correctly persist Performer Script if name just exist but new version released")
    void persistNameExistNewVersion() {
        FileRepresentation fileRepresentation = factorizeFileRepresentationNextVersion();

        RegistryStatus status = fileInteractor.persist(fileRepresentation);

        assertEquals(RegistryStatus.SAVED, status);
    }

    @Test
    @DisplayName("Should cannot override just existing script")
    void persistJustExist() {
        FileRepresentation fileRepresentation = factorizeFileRepresentation();

        fileInteractor.persist(fileRepresentation);
        RegistryStatus status = fileInteractor.persist(fileRepresentation);

        assertEquals(RegistryStatus.FILE_JUST_EXISTS, status);
    }

    @AfterAll
    static void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(DIRECTORY_PATH));
    }
}