package org.easeci.registry.domain.api.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.RegistryStatus;
import org.easeci.registry.domain.plugin.PluginDocumentationRequest;
import org.easeci.registry.domain.plugin.PluginDocumentationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/plugin/management")
class PluginRestManagementController {
    private PluginDocumentationService pluginDocumentationService;

    @PostMapping("/documentation/add")
    @ResponseStatus(HttpStatus.OK)
    Mono<RegistryStatus> addDocumentation(@RequestBody @Valid PluginDocumentationRequest request, Principal principal) {
        return pluginDocumentationService.addDocumentation(principal, request);
    }

    @PostMapping("/documentation/edit")
    @ResponseStatus(HttpStatus.OK)
    Mono<RegistryStatus> editDocumentation(@RequestBody @Valid PluginDocumentationRequest request, Principal principal) {
        return pluginDocumentationService.editDocumentation(principal, request);
    }
}
