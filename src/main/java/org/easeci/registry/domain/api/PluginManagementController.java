package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.Principal;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/management/plugins")
class PluginManagementController {
    private PerformerManagerService performerManagerService;

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
}
