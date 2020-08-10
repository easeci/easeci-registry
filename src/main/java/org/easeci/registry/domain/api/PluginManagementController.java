package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.easeci.registry.domain.plugin.PluginDocumentationRequest;
import org.easeci.registry.domain.plugin.PluginDocumentationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

import static org.easeci.registry.domain.files.RegistryStatus.DOCUMENTATION_JUST_EXISTS;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/management/plugins")
class PluginManagementController {
    private PerformerManagerService performerManagerService;
    private PluginDocumentationService pluginDocumentationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String getAllPluginsOfUser(Principal principal, Model model) {
        performerManagerService.getPerformerPage(principal, 0, 20)
                .subscribe(performerResponses -> {
                    model.addAttribute("hasNoPerformers", performerResponses.isEmpty());
                    model.addAttribute("page", performerResponses);
                });
        return "performers-management-view";
    }

    @GetMapping("/{pluginName}/{pluginVersion}")
    @ResponseStatus(HttpStatus.OK)
    String pluginDocumentationManagementView(@PathVariable String pluginName, @PathVariable String pluginVersion, Principal principal, Model model) {
        pluginDocumentationService.findDocumentation(principal, pluginName, pluginVersion)
                .subscribe(pluginDocumentationResponse -> {
                    model.addAttribute("doc", pluginDocumentationResponse);
                    model.addAttribute("request", new PluginDocumentationRequest(pluginName, pluginVersion, pluginDocumentationResponse.getDocumentationText()));
                });
        return "performer-doc-management-view";
    }

    @PostMapping("/documentation")
    @ResponseStatus(HttpStatus.OK)
    String addPluginDocumentation(@Valid @ModelAttribute("request") PluginDocumentationRequest request, Principal principal, Model model) {
        pluginDocumentationService.addDocumentation(principal, request)
                .subscribe(addStatus -> {
                    if (addStatus.equals(DOCUMENTATION_JUST_EXISTS)) {
                        pluginDocumentationService.editDocumentation(principal, request)
                                .subscribe(editStatus -> {
                                    model.addAttribute("status", editStatus);
                                    findDocumentation(principal, request, model);
                                });
                    } else {
                        model.addAttribute("status", addStatus);
                        findDocumentation(principal, request, model);
                    }
                });
        return "performer-doc-management-view";
    }

    private void findDocumentation(Principal principal, PluginDocumentationRequest request, Model model) {
        pluginDocumentationService.findDocumentation(principal, request.getPluginName(), request.getPluginVersion())
                .subscribe(pluginDocumentationResponse -> {
                    model.addAttribute("doc", pluginDocumentationResponse);
                    model.addAttribute("request", new PluginDocumentationRequest(request.getPluginName(), request.getPluginVersion(), pluginDocumentationResponse.getDocumentationText()));
                });
    }

}
