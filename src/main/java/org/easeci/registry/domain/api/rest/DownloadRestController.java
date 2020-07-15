package org.easeci.registry.domain.api.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.ErrorResponse;
import org.easeci.registry.domain.files.ExtensionNotExistsException;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@ControllerAdvice
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/download")
class DownloadRestController {
    private PerformerManagerService performerManagerService;

    @GetMapping(value = "/{name}/{version}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    Mono<byte[]> downloadPerformer(@PathVariable String name, @PathVariable String version) {
        log.info("=> File download request, name={}, version={}", name, version);
        return performerManagerService.findFile(name, version);
    }

    @ExceptionHandler(ExtensionNotExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handle(ExtensionNotExistsException ex) {
        return ResponseEntity.of(Optional.of(new ErrorResponse(ex)));
    }
}
