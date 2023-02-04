package org.easeci.registry.domain.files;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.files.dto.PerformerVersionBasic;
import org.easeci.registry.domain.token.UploadTokenService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

    private static Principal principal = Mockito.mock(Principal.class);

    @BeforeAll
    static void setup() throws IOException {
        Files.createDirectories(Paths.get(DIRECTORY_PATH));

        Mockito.when(principal.getName()).thenReturn("EaseCI");
    }

    @Test
    @DisplayName("Should save entity with success and should save file in specified path " +
            "-> performer with this name not exists before")
    void shouldSave() {
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("EaseCI");

        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();

        performerManagerService.uploadProcess(fileUploadRequest, principal);

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

        performerManagerService.uploadProcess(fileUploadRequest, principal);
        performerManagerService.uploadProcess(fileUploadRequestNext, principal);

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

        performerManagerService.uploadProcess(fileUploadRequest, principal);
        performerManagerService.uploadProcess(fileUploadRequest, principal);
    }

    @Test
    @DisplayName("Should get page correctly with few elements in repository")
    void shouldGetPage() {
        Mockito.when(tokenService.isTokenAvailable(any())).thenReturn(true);

        final int page = 0;
        final int size = 10;
        FileUploadRequest fileUploadRequest = factorizeFileUploadRequest();
        FileUploadRequest fileUploadRequestNext = factorizeFileUploadRequestNextVersion();

        performerManagerService.uploadProcess(fileUploadRequest, principal);
        performerManagerService.uploadProcess(fileUploadRequestNext, principal);

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

        performerManagerService.uploadProcess(fileUploadRequest, principal);
        performerManagerService.uploadProcess(fileUploadRequestNext, principal);

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

    @Test
    @DisplayName("Should correctly return 'true' when there are newest version of plugin/performer")
    void hasPluginNewestVersionTest() {
        final String PLUGIN_NAME = "git";
        final String PLUGIN_VERSION = "0.0.5";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        Mockito.when(localVersionRepository.findAllByPerformerName(PLUGIN_NAME))
                .thenReturn(provide());

        boolean hasNewestVersion = localService.hasPluginNewestVersion(PLUGIN_NAME, PLUGIN_VERSION);

        assertTrue(hasNewestVersion);
    }

    @Test
    @DisplayName("Should correctly return 'false' when there are no newest version")
    void hasPluginNewestVersionFalseTest() {
        final String PLUGIN_NAME = "git";
        final String PLUGIN_VERSION = "0.0.6";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        Mockito.when(localVersionRepository.findAllByPerformerName(PLUGIN_NAME))
                .thenReturn(provide());

        boolean hasNewestVersion = localService.hasPluginNewestVersion(PLUGIN_NAME, PLUGIN_VERSION);

        assertFalse(hasNewestVersion);
    }

    @Test
    @DisplayName("Should correctly return 'false' where there is no such plugin in database")
    void hasPluginNewestVersionNoFoundAnyEntitiesTest() {
        final String PLUGIN_NAME = "jenkins";
        final String PLUGIN_VERSION = "0.0.6";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        boolean hasNewestVersion = localService.hasPluginNewestVersion(PLUGIN_NAME, PLUGIN_VERSION);

        assertFalse(hasNewestVersion);
    }

    @Test
    @DisplayName("Should not find any newest version of plugin when requested version is latest")
    void findNewerVersionsNoVersionsTest() {
        final String PLUGIN_NAME = "git";
        final String PLUGIN_VERSION = "0.0.6";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        Mockito.when(localVersionRepository.findAllByPerformerName(PLUGIN_NAME))
                .thenReturn(provide());

        List<PerformerVersionBasic> newerVersion = localService.findNewerVersions(PLUGIN_NAME, PLUGIN_VERSION);

        assertEquals(0, newerVersion.size());
    }

    @Test
    @DisplayName("Should not find any newest version of plugin when requested version is latest")
    void findNewerVersionsNoVersionsTest222() {
        final String PLUGIN_NAME = "git";
        final String PLUGIN_VERSION = "0.0.6";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        Mockito.when(localVersionRepository.findAllByPerformerName(PLUGIN_NAME))
                .thenReturn(provide());

        List<PerformerVersionBasic> newerVersion = localService.findNewerVersions(PLUGIN_NAME, PLUGIN_VERSION);

        assertEquals(0, newerVersion.size());
    }

    @ParameterizedTest
    @MethodSource("provideVersionWithSize")
    @DisplayName("Should correctly find newer version than requested")
    void findNewerVersionsTest(String pluginVersion, int newestVersionSize) {
        final String PLUGIN_NAME = "git";

        PerformerVersionRepository localVersionRepository = Mockito.mock(PerformerVersionRepository.class);
        PerformerManagerService localService = new PerformerManagerService(null, null, localVersionRepository, null, null);

        Mockito.when(localVersionRepository.findAllByPerformerName(PLUGIN_NAME))
                .thenReturn(provide());

        List<PerformerVersionBasic> newerVersion = localService.findNewerVersions(PLUGIN_NAME, pluginVersion);

        assertEquals(newestVersionSize, newerVersion.size());
    }

    private static Stream<Arguments> provideVersionWithSize() {
        return Stream.of(
                Arguments.of("0.0.6", 0),
                Arguments.of("0.0.5", 1),
                Arguments.of("0.0.4", 2),
                Arguments.of("0.0.3", 3),
                Arguments.of("0.0.2", 4),
                Arguments.of("0.0.1", 5),
                Arguments.of("0.0.0", 6) // if there is no such version in database, return all versions
        );
    }

    private static List<PerformerVersionBasic> provide() {
        return new ArrayList<>(List.of(
                new PerformerVersionBasic(1L, "0.0.1", LocalDateTime.now()),
                new PerformerVersionBasic(2L, "0.0.2", LocalDateTime.now()),
                new PerformerVersionBasic(3L, "0.0.3", LocalDateTime.now()),
                new PerformerVersionBasic(4L, "0.0.4", LocalDateTime.now()),
                new PerformerVersionBasic(5L, "0.0.5", LocalDateTime.now()),
                new PerformerVersionBasic(6L, "0.0.6", LocalDateTime.now())
        ));
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