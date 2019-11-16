package org.easeci.registry.domain.files;

import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class FilesFacadeService {

    public Mono<FileUploadResponse> uploadProcess(FileUploadRequest request) {
        try {
            System.out.println(new String(request.getMultipartFile().getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Mono.just(FileRepresentation.builder()
                .build())
                .map(fileRepresentation -> FileUploadResponse.builder()
                        .build());
    }
}
