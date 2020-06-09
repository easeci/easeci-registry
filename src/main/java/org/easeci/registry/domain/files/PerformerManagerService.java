package org.easeci.registry.domain.files;

import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;
import org.easeci.registry.domain.files.dto.PerformerResponse;
import org.easeci.registry.domain.files.dto.PerformerVersionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class PerformerManagerService {
    private FileInteractor fileInteractor;
    private PerformerRepository performerRepository;

    @Value("${tmp.dir}") private String temporaryStorage;
    @Value("${file.extension}") private String fileExtension;

    public PerformerManagerService(FileInteractor fileInteractor, PerformerRepository performerRepository) {
        this.fileInteractor = fileInteractor;
        this.performerRepository = performerRepository;
    }

    public FileUploadResponse uploadProcess(FileUploadRequest request) {
        FileRepresentation fileRepresentation = prepare(request);
        AddPerformerResponse addPerformerResponse = JarFileValidator.of(performerRepository, temporaryStorage, fileExtension).check(fileRepresentation);
        if (!addPerformerResponse.isAddedCorrectly()) {
            return FileUploadResponse.builder()
                    .isUploaded(false)
                    .status(RegistryStatus.INVALID_REJECTED)
                    .validationErrorList(addPerformerResponse.getValidationErrorList())
                    .build();
        }
        saveVersion(fileRepresentation);
        RegistryStatus persistStatus = fileInteractor.persist(fileRepresentation);

        return FileUploadResponse.builder()
                .isUploaded(true)
                .status(persistStatus)
                .meta(fileRepresentation.getMeta())
                .build();
    }

    private FileRepresentation prepare(FileUploadRequest request) {
        return FileRepresentation.builder()
                .status(RegistryStatus.JUST_PROCESSING)
                .meta(FileRepresentation.FileMeta.builder()
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

    private void saveVersion(FileRepresentation fileRepresentation) {
        if (isNull(fileRepresentation)) {
            throw new RuntimeException("Cannot process when FileRepresentation is null");
        }
        performerRepository.findByPerformerName(fileRepresentation.getMeta().getPerformerName())
                .ifPresentOrElse(performerEntity -> {
                            performerEntity.getPerformerVersions().add(prepare(fileRepresentation, performerEntity));
                            performerRepository.save(performerEntity);
                        },
                        () -> {
                            PerformerEntity savedEntity = performerRepository.save(PerformerEntity.builder()
                                    .authorFullname(fileRepresentation.getMeta().getAuthorFullname())
                                    .authorEmail(fileRepresentation.getMeta().getAuthorEmail())
                                    .company(fileRepresentation.getMeta().getCompany())
                                    .creationDate(fileRepresentation.getMeta().getCreationDate())
                                    .performerName(fileRepresentation.getMeta().getPerformerName())
                                    .build());
                            Set<PerformerVersionEntity> versionEntities = new HashSet<>();
                            versionEntities.add(prepare(fileRepresentation, savedEntity));
                            savedEntity.setPerformerVersions(versionEntities);
                            performerRepository.save(savedEntity);
                        });
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
        return Mono.just(PageRequest.of(page, size))
                .map(pageRequest -> performerRepository.findAll(pageRequest))
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
}
