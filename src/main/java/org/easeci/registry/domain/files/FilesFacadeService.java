package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FilesFacadeService {
    private FileInteractor fileInteractor;

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

    public PaginatedSet<FileRepresentation.FileMeta> findByPage(int page) {
        PageRequest pageRequest = PageRequest.of(page);
        return fileInteractor.listAll(pageRequest);
    }

    public List<FileRepresentation.FileMeta> getAllVersionsByName(String performerName) {
        return fileInteractor.get(performerName);
    }

    public String getDescription(String performerName) {
        return "";
    }
}
