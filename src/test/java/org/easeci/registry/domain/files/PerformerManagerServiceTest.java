package org.easeci.registry.domain.files;

import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.easeci.registry.utils.CommonUtils.factorizeFileUploadRequest;
import static org.easeci.registry.utils.CommonUtils.factorizeFileUploadRequestNextVersion;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({ "test" })
class PerformerManagerServiceTest {

    @Autowired
    private PerformerManagerService performerManagerService;

    @Autowired
    private PerformerRepository performerRepository;

    @Test
    @DisplayName("Should save entity with success and should save file in specified path " +
            "-> performer with this name not exists before")
    void shouldSave() {
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();

        performerManagerService.uploadProcess(fileUploadRequest).subscribe();

        PerformerEntity performerEntity = performerRepository.findByPerformerName(fileUploadRequest.getPerformerName()).orElseThrow();
        PerformerVersionEntity versionEntity = performerEntity.getPerformerVersions().iterator().next();
        assertAll(() -> assertEquals(fileUploadRequest.getAuthorEmail(), performerEntity.getAuthorEmail()),
                () -> assertEquals(fileUploadRequest.getAuthorFullname(), performerEntity.getAuthorFullname()),
                () -> assertEquals(fileUploadRequest.getCompany(), performerEntity.getCompany()),
                () -> assertEquals(fileUploadRequest.getPerformerVersion(), versionEntity.getPerformerVersion()),
                () -> assertEquals(fileUploadRequest.getMultipartFile().length, versionEntity.getPerformerScriptBytes()));
    }

    @Test
    @DisplayName("Should save version entity with success and should save file in specified path " +
            "-> performer with this name just exist and only new version should be appended")
    void shouldSaveVersion() {
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();
        FileUploadRequest fileUploadRequestNext = factorizeFileUploadRequestNextVersion();

        performerManagerService.uploadProcess(fileUploadRequest).subscribe();
        performerManagerService.uploadProcess(fileUploadRequestNext).subscribe();

        PerformerEntity performerEntity = performerRepository.findByPerformerName(fileUploadRequest.getPerformerName()).orElseThrow();

        assertAll(() -> assertEquals(fileUploadRequest.getAuthorEmail(), performerEntity.getAuthorEmail()),
                () -> assertEquals(fileUploadRequest.getAuthorFullname(), performerEntity.getAuthorFullname()),
                () -> assertEquals(fileUploadRequest.getCompany(), performerEntity.getCompany()),
                () -> assertEquals(2, performerEntity.getPerformerVersions().size()));
    }

    @AfterEach
    void tearDown() {
        performerRepository.deleteAll();
    }
}