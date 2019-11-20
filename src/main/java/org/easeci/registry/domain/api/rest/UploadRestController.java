package org.easeci.registry.domain.api.rest;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
class UploadRestController {
    private PerformerManagerService filesFacadeService;

    @PostMapping("/performer")
    @ResponseStatus(HttpStatus.OK)
    Mono<FileUploadResponse> uploadPerformer(@RequestParam("file") MultipartFile multipartFile,
                                             @RequestHeader("author-fullname") String authorFullname,
                                             @RequestHeader("author-email") String authorEmail,
                                             @RequestHeader("author-company") String company,
                                             @RequestHeader("performer-name") String performerName,
                                             @RequestHeader("performer-version") String performerVersion) throws IOException {

        return filesFacadeService.uploadProcess(FileUploadRequest.builder()
                        .authorFullname(authorFullname)
                        .authorEmail(authorEmail)
                        .company(company)
                        .performerName(performerName)
                        .performerVersion(performerVersion)
                        .multipartFile(multipartFile.getBytes())
                        .build());
    }
}
