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
import static org.junit.jupiter.api.Assertions.*;

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

        performerManagerService.uploadProcess(fileUploadRequest);

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

        performerManagerService.uploadProcess(fileUploadRequest);
        performerManagerService.uploadProcess(fileUploadRequestNext);

        PerformerEntity performerEntity = performerRepository.findByPerformerName(fileUploadRequest.getPerformerName()).orElseThrow();

        assertAll(() -> assertEquals(fileUploadRequest.getAuthorEmail(), performerEntity.getAuthorEmail()),
                () -> assertEquals(fileUploadRequest.getAuthorFullname(), performerEntity.getAuthorFullname()),
                () -> assertEquals(fileUploadRequest.getCompany(), performerEntity.getCompany()),
                () -> assertEquals(2, performerEntity.getPerformerVersions().size()));
    }

    @Test
    @DisplayName("Should not save entity and should not override file when trying to upload" +
            " the same request twice")
    void shouldNotSave() {
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();

        performerManagerService.uploadProcess(fileUploadRequest);
        performerManagerService.uploadProcess(fileUploadRequest);
    }

    @Test
    @DisplayName("Should get page correctly with few elements in repository")
    void shouldGetPage() {
        final int page = 0;
        final int size = 10;
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();
        FileUploadRequest fileUploadRequestNext = factorizeFileUploadRequestNextVersion();

        performerManagerService.uploadProcess(fileUploadRequest);
        performerManagerService.uploadProcess(fileUploadRequestNext);

        performerManagerService.getPerformerPage(page, size)
                .subscribe(performerEntities -> {
                    assertEquals(1, performerEntities.getTotalElements());
                    assertEquals(1, performerEntities.getTotalPages());
                });
    }

    @Test
    @DisplayName("Should get all versions of Performer by specified name with success")
    void shouldGetAllVersions() {
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();
        FileUploadRequest fileUploadRequestNext = factorizeFileUploadRequestNextVersion();

        performerManagerService.uploadProcess(fileUploadRequest);
        performerManagerService.uploadProcess(fileUploadRequestNext);

        performerManagerService.getAllVersionsByName(fileUploadRequest.getPerformerName())
                .subscribe(versions -> {
                    assertEquals(2, versions.size());
                    versions.stream()
                            .peek(performerVersion ->
                                    assertTrue(performerVersion.getPerformerVersion().equals(fileUploadRequest.getPerformerVersion()) ||
                                            performerVersion.getPerformerVersion().equals(fileUploadRequestNext.getPerformerVersion())))
                            .close();
                });
    }

    @AfterEach
    void tearDown() {
        performerRepository.deleteAll();
    }
}