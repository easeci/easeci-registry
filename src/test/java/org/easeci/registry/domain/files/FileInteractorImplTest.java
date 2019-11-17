package org.easeci.registry.domain.files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.easeci.registry.utils.CommonUtils.factorizeFileRepresentation;
import static org.easeci.registry.utils.CommonUtils.factorizeFileRepresentationNextVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({ "test" })
class FileInteractorImplTest {

    @Autowired
    private FileInteractor fileInteractor;

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
}