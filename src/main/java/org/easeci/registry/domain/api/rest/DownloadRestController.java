package org.easeci.registry.domain.api.rest;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/download")
class DownloadRestController {
    private PerformerManagerService performerManagerService;

    @GetMapping(value = "/{name}/{version}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    byte[] downloadPerformer(@PathVariable String name, @PathVariable String version) {
        return performerManagerService.findFile(name, version);
    }
}
