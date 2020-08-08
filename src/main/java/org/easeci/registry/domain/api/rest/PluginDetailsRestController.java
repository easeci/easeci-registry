package org.easeci.registry.domain.api.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.easeci.registry.domain.files.dto.PerformerDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/details")
class PluginDetailsRestController {
    private PerformerManagerService performerManagerService;

    @GetMapping(value = "/{name}/{version}")
    @ResponseStatus(HttpStatus.OK)
    Mono<PerformerDetailsResponse> downloadPerformer(@PathVariable String name, @PathVariable String version) {
        log.info("=> Get plugin details request, name={}, version={}", name, version);
        return performerManagerService.findDetails(name, version);
    }
}
