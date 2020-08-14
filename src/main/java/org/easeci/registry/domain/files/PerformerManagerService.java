package org.easeci.registry.domain.files;

import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.files.dto.*;
import org.easeci.registry.domain.plugin.PluginDocumentationRepository;
import org.easeci.registry.domain.plugin.dto.DocumentationDto;
import org.easeci.registry.domain.token.UploadTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.easeci.registry.domain.files.RegistryStatus.*;

@Slf4j
@Service
public class PerformerManagerService {
    private FileInteractor fileInteractor;
    private PerformerRepository performerRepository;
    private PerformerVersionRepository performerVersionRepository;
    private PluginDocumentationRepository pluginDocumentationRepository;
    private UploadTokenService tokenService;

    @Value("${tmp.dir}")
    private String temporaryStorage;
    @Value("${file.extension}")
    private String fileExtension;

    public PerformerManagerService(FileInteractor fileInteractor, PerformerRepository performerRepository,
                                   PerformerVersionRepository performerVersionRepository, PluginDocumentationRepository pluginDocumentationRepository,
                                   UploadTokenService tokenService) {
        this.fileInteractor = fileInteractor;
        this.performerRepository = performerRepository;
        this.performerVersionRepository = performerVersionRepository;
        this.pluginDocumentationRepository = pluginDocumentationRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public FileUploadResponse uploadProcess(FileUploadRequest request, Principal principal) {
        FileRepresentation fileRepresentation = prepare(request, principal);
        AddPerformerResponse addPerformerResponse = JarFileValidator.of(tokenService, performerRepository, new JarFileValidatorHelper(temporaryStorage, fileExtension))
                .validationChain(fileRepresentation);
        log.info("Validation result: {}", addPerformerResponse.toString());
        if (!addPerformerResponse.isAddedCorrectly()) {
            return FileUploadResponse.builder()
                    .isUploaded(false)
                    .status(RegistryStatus.INVALID_REJECTED)
                    .validationErrorList(addPerformerResponse.getValidationErrorList())
                    .build();
        }
        Long performerVersionId = saveVersion(fileRepresentation);
        tokenService.use(requireNonNull(addPerformerResponse.getToken()), performerVersionId);
        RegistryStatus persistStatus = fileInteractor.persist(fileRepresentation);

        return FileUploadResponse.builder()
                .isUploaded(persistStatus.equals(RegistryStatus.SAVED))
                .status(persistStatus)
                .meta(fileRepresentation.getMeta())
                .validationErrorList(List.of(persistStatus.getValidationError()))
                .build();
    }

    private FileRepresentation prepare(FileUploadRequest request, Principal principal) {
        return FileRepresentation.builder()
                .status(RegistryStatus.JUST_PROCESSING)
                .meta(FileRepresentation.FileMeta.builder()
                        .uploaderPrincipalName(principal.getName())
                        .authorFullname(request.getAuthorFullname())
                        .authorEmail(request.getAuthorEmail())
                        .company(request.getCompany())
                        .creationDate(LocalDateTime.now())
                        .performerName(request.getPerformerName())
                        .performerVersion(request.getPerformerVersion())
                        .performerScriptBytes(request.getMultipartFile().length)
                        .validated(false)
                        .build())
                .payload(request.getMultipartFile())
                .build();
    }

    private Long saveVersion(FileRepresentation fileRepresentation) {
        if (isNull(fileRepresentation)) {
            throw new RuntimeException("Cannot process when FileRepresentation is null");
        }
        return performerRepository.findByPerformerName(fileRepresentation.getMeta().getPerformerName())
                .map(performerEntity -> {
                    performerEntity.getPerformerVersions().add(prepare(fileRepresentation, performerEntity));
                    return performerRepository.save(performerEntity);
                }).orElseGet(() -> {
                    PerformerEntity savedEntity = performerRepository.save(PerformerEntity.builder()
                            .uploaderPrincipalName(fileRepresentation.getMeta().getUploaderPrincipalName())
                            .authorFullname(fileRepresentation.getMeta().getAuthorFullname())
                            .authorEmail(fileRepresentation.getMeta().getAuthorEmail())
                            .company(fileRepresentation.getMeta().getCompany())
                            .creationDate(fileRepresentation.getMeta().getCreationDate())
                            .performerName(fileRepresentation.getMeta().getPerformerName())
                            .build());
                    Set<PerformerVersionEntity> versionEntities = new HashSet<>();
                    versionEntities.add(prepare(fileRepresentation, savedEntity));
                    savedEntity.setPerformerVersions(versionEntities);
                    return performerRepository.save(savedEntity);
                }).getPerformerVersions().stream()
                .filter(performerVersionEntity -> performerVersionEntity.getPerformerVersion().equals(fileRepresentation.getMeta().getPerformerVersion()))
                .findAny()
                .orElseThrow()
                .getVersionId();
    }

    private PerformerVersionEntity prepare(FileRepresentation fileRepresentation, PerformerEntity performerEntity) {
        return PerformerVersionEntity.builder()
                .performer(performerEntity)
                .performerVersion(fileRepresentation.getMeta().getPerformerVersion())
                .performerScriptBytes(fileRepresentation.getPayload().length)
                .validated(false)
                .releaseDate(LocalDateTime.now())
                .build();
    }

    public Mono<Page<PerformerResponse>> getPerformerPage(int page, int size) {
        return findAndMap(performerRepository -> performerRepository.findAll(PageRequest.of(page, size)));
    }

    public Mono<Page<PerformerResponse>> getPerformerPage(Principal principal, int page, int size) {
        return findAndMap(performerRepository -> performerRepository.findAllByUploaderPrincipalName(principal.getName(), PageRequest.of(page, size)));
    }

    private Mono<Page<PerformerResponse>> findAndMap(Function<PerformerRepository, Page<PerformerEntity>> fetchFunction) {
        return Mono.just(fetchFunction)
                .map(fun -> fun.apply(this.performerRepository))
                .map(performerEntities -> performerEntities.map(performerEntity -> PerformerResponse.builder()
                        .performerId(performerEntity.getPerformerId())
                        .authorFullname(performerEntity.getAuthorFullname())
                        .authorEmail(performerEntity.getAuthorEmail())
                        .company(performerEntity.getCompany())
                        .creationDate(performerEntity.getCreationDate())
                        .performerName(performerEntity.getPerformerName())
                        .description(performerEntity.getDescription())
                        .performerVersions(performerEntity.getPerformerVersions().stream()
                                .map(performerVersionEntity -> PerformerVersionResponse.builder()
                                        .versionId(performerVersionEntity.getVersionId())
                                        .performerVersion(performerVersionEntity.getPerformerVersion())
                                        .performerScriptBytes(performerVersionEntity.getPerformerScriptBytes())
                                        .validated(performerVersionEntity.getValidated())
                                        .releaseDate(performerVersionEntity.getReleaseDate())
                                        .documentationUrl(performerVersionEntity.getDocumentationUrl())
                                        .build())
                                .collect(Collectors.toSet()))
                        .build()));
    }

    public Mono<Set<PerformerVersionResponse>> getAllVersionsByName(String performerName) {
        return Mono.just(performerName)
                .map(name -> performerRepository.findAllVersionByName(performerName));
    }

    public void saveDescription(String performerName, String description) {
        performerRepository.updateDescription(performerName, description);
    }

    public String getDescription(String performerName) {
        return performerRepository.findDescription(performerName);
    }

    public Mono<byte[]> findFile(String performerName, String performerVersion) {
        return Mono.just(fileInteractor.get(performerName, performerVersion))
                .filter(Objects::nonNull)
                .map(FileRepresentation::getPayload)
                .onErrorMap(throwable -> new ExtensionNotExistsException(performerName, performerVersion));
    }

    public Mono<PerformerDetailsResponse> findDetails(String name, String version, boolean fetchDocumentation) {
        return Mono.just(performerRepository.findByPerformerName(name))
                .map(Optional::orElseThrow)
                .doOnNext(performerEntity -> performerEntity.setPerformerVersions(
                        performerEntity.getPerformerVersions()
                                .stream()
                                .filter(versionEntity -> versionEntity.getPerformerVersion().equals(version))
                                .collect(Collectors.toSet())))
                .map(this::map)
                .doOnNext(performerDetailsResponse -> {
                    if (fetchDocumentation)
                        performerDetailsResponse.setDocumentationText(
                                this.pluginDocumentationRepository.findPluginDocumentationText(name, version)
                                                                  .map(DocumentationDto::getDocumentationTextAsBytes)
                                                                  .orElse(new byte[] {})
                        );
                })
                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return performerNotFound();
                })
                .switchIfEmpty(performerNotFound());
    }

    private PerformerDetailsResponse map(PerformerEntity performerEntity) {
        final RegistryStatus STATUS = FOUND;

        if (performerEntity.getPerformerVersions().size() > 1) {
            return PerformerDetailsResponse.builder()
                    .status(PLUGIN_MORE_THAN_ONE_RESULTS)
                    .message(PLUGIN_MORE_THAN_ONE_RESULTS.getValidationError().getMessage())
                    .build();
        }

        PerformerVersionEntity versionEntity = performerEntity.getPerformerVersions().stream()
                .findFirst()
                .orElseThrow();

        List<PerformerVersionBasic> newerVersions = this.findNewerVersions(performerEntity.getPerformerName(), versionEntity.getPerformerVersion());
        return PerformerDetailsResponse.builder()
                .status(STATUS)
                .message(STATUS.getValidationError().getMessage())
                .performerId(performerEntity.getPerformerId())
                .authorFullname(performerEntity.getAuthorFullname())
                .authorEmail(performerEntity.getAuthorEmail())
                .company(performerEntity.getCompany())
                .creationDate(performerEntity.getCreationDate())
                .performerName(performerEntity.getPerformerName())
                .description(performerEntity.getDescription())
                .newerPerformerVersions(newerVersions)
                .isNewerVersionAvailable(!newerVersions.isEmpty())
                .performerVersions(Set.of(PerformerVersionResponse.builder()
                        .versionId(versionEntity.getVersionId())
                        .performerVersion(versionEntity.getPerformerVersion())
                        .performerScriptBytes(versionEntity.getPerformerScriptBytes())
                        .validated(versionEntity.getValidated())
                        .releaseDate(versionEntity.getReleaseDate())
                        .documentationUrl(versionEntity.getDocumentationUrl())
                        .build()))
                .build();
    }

    private Mono<PerformerDetailsResponse> performerNotFound() {
        final RegistryStatus STATUS = PLUGIN_NOT_FOUND;
        return Mono.just(PerformerDetailsResponse.builder()
                .status(STATUS)
                .message(STATUS.getValidationError().getMessage())
                .build());
    }

    public boolean hasPluginNewestVersion(String pluginName, String pluginVersion) {
        List<PerformerVersionBasic> allByPerformerName = performerVersionRepository.findAllByPerformerName(pluginName);
        if (allByPerformerName.isEmpty()) {
            return false;
        }
        allByPerformerName.sort(Comparator.comparingLong(PerformerVersionBasic::getVersionId));
        for (PerformerVersionBasic v : allByPerformerName) {
            if (v.getPerformerVersion().equals(pluginVersion)) {
                int index = allByPerformerName.indexOf(v);
                if (index == allByPerformerName.size() - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<PerformerVersionBasic> findNewerVersions(String pluginName, String pluginVersion) {
        List<PerformerVersionBasic> allByPerformerName = performerVersionRepository.findAllByPerformerName(pluginName);
        allByPerformerName.sort(Comparator.comparingLong(PerformerVersionBasic::getVersionId));
        boolean pluginVersionExists = false;
        List<PerformerVersionBasic> newestVersions = new ArrayList<>(1);
        for (PerformerVersionBasic v : allByPerformerName) {
            if (v.getPerformerVersion().equals(pluginVersion)) {
                pluginVersionExists = true;
                int index = allByPerformerName.indexOf(v);
                for (int i = index + 1; i < allByPerformerName.size(); i++) {
                    newestVersions.add(allByPerformerName.get(i));
                }
            }
        }
        return (newestVersions.isEmpty() && !pluginVersionExists) ? allByPerformerName : newestVersions;
    }

    public Mono<PerformerUpdateCheckResponse> findUpdates(String name, String version) {
        List<PerformerVersionBasic> newerVersions = findNewerVersions(name, version);
        PerformerUpdateCheckResponse updateCheck = PerformerUpdateCheckResponse.builder()
                .newerPerformerVersions(newerVersions)
                .isNewerVersionAvailable(!newerVersions.isEmpty())
                .status(FOUND)
                .message(FOUND.getValidationError().getMessage())
                .build();

        return Mono.just(updateCheck)
                .onErrorReturn(PerformerUpdateCheckResponse.builder()
                        .status(NOT_FOUND)
                        .message(NOT_FOUND.getValidationError().getMessage())
                        .build());
    }
}
