package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.files.FilesFacadeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
class UploadRestController {
    private FilesFacadeService filesFacadeService;

    @PostMapping("/performer")
    @ResponseStatus(HttpStatus.OK)
    Mono<FileUploadResponse> uploadPerformer(@RequestHeader("author-fullname") String authorFullname,
                                             @RequestHeader("author-email") String authorEmail,
                                             @RequestHeader("author-company") String company,
                                             @RequestHeader("performer-name") String performerName,
                                             @RequestHeader("performer-version") String performerVersion,
                                             @RequestParam("script") MultipartFile multipartFile) {

        return filesFacadeService.uploadProcess(FileUploadRequest.builder()
                        .authorFullname(authorFullname)
                        .authorEmail(authorEmail)
                        .company(company)
                        .performerName(performerName)
                        .performerVersion(performerVersion)
                        .multipartFile(multipartFile)
                        .build());
    }
}
