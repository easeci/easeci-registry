package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@AllArgsConstructor
public class PerformerManagerService {
    private FileInteractor fileInteractor;
    private PerformerRepository performerRepository;

    public Mono<FileUploadResponse> uploadProcess(FileUploadRequest request) {
        return Mono.just(FileRepresentation.builder()
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
                    .build())
                .doOnNext(IncomeFileValidator::valid)
                .doOnSuccess(this::saveVersion)
                .doOnSuccess(fileRepresentation -> fileInteractor.persist(fileRepresentation))
                .map(fileRepresentation -> FileUploadResponse.builder()
                        .status(RegistryStatus.SAVED)
                        .meta(fileRepresentation.getMeta())
                        .build())
                .onErrorReturn(FileUploadResponse.builder()
                        .status(RegistryStatus.INVALID_REJECTED)
                        .meta(null)
                        .build());
    }

    private void saveVersion(FileRepresentation fileRepresentation) {
        performerRepository.findByPerformerName(fileRepresentation.getMeta().getPerformerName())
                .ifPresentOrElse(performerEntity -> {
                            String performerName = fileRepresentation.getMeta().getPerformerName();
                            String performerVersion = fileRepresentation.getMeta().getPerformerVersion();
                            if (performerRepository.isVersionExists(performerName, performerVersion) > 0) {
                                throw new EntityExistsException("Performer with name: " + performerEntity.getPerformerName()
                                + ", and with version: " + performerEntity.getPerformerVersions()
                                + " just exists in database!");
                            } else {
                                performerEntity.getPerformerVersions().add(prepare(fileRepresentation, performerEntity));
                                performerRepository.save(performerEntity);
                            }
                        },
                        () -> {
                            PerformerEntity savedEntity = performerRepository.save(PerformerEntity.builder()
                                    .authorFullname(fileRepresentation.getMeta().getAuthorFullname())
                                    .authorEmail(fileRepresentation.getMeta().getAuthorEmail())
                                    .company(fileRepresentation.getMeta().getCompany())
                                    .creationDate(fileRepresentation.getMeta().getCreationDate())
                                    .performerName(fileRepresentation.getMeta().getPerformerName())
                                    .build());
                            savedEntity.setPerformerVersions(Set.of(prepare(fileRepresentation, savedEntity)));
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

    public Mono<Page<PerformerEntity>> getPerformerPage(int page, int size) {
        return Mono.just(PageRequest.of(page, size))
                .map(pageRequest -> performerRepository.findAll(pageRequest));
    }
}
