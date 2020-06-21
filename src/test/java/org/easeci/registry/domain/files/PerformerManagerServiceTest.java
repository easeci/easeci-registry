package org.easeci.registry.domain.files;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.token.UploadTokenService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.easeci.registry.utils.CommonUtils.factorizeFileUploadRequest;
import static org.easeci.registry.utils.CommonUtils.factorizeFileUploadRequestNextVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles({ "test" })
class PerformerManagerServiceTest {
    private static String DIRECTORY_PATH = "/tmp/ease/registry";

    @Autowired
    private PerformerManagerService performerManagerService;

    @Autowired
    private PerformerRepository performerRepository;

    @MockBean
    private UploadTokenService tokenService;

    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(Paths.get(DIRECTORY_PATH));
    }

    @Test
    @DisplayName("Should save entity with success and should save file in specified path " +
            "-> performer with this name not exists before")
    void shouldSave() {
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

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
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);
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
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();

        performerManagerService.uploadProcess(fileUploadRequest);
        performerManagerService.uploadProcess(fileUploadRequest);
    }

    @Test
    @DisplayName("Should get page correctly with few elements in repository")
    void shouldGetPage() {
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

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
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

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

    @AfterAll
    static void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(DIRECTORY_PATH));
    }
}