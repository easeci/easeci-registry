package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.easeci.registry.domain.files.dto.PerformerResponse;
import org.easeci.registry.domain.files.dto.PerformerVersionResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.easeci.registry.domain.api.utils.PageUtils.transformPageNum;

@Controller
@AllArgsConstructor
@RequestMapping("/")
class RegistryController {
    private PerformerManagerService performerManagerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String home() {
        return "home";
    }

    @GetMapping("/performers")
    @ResponseStatus(HttpStatus.OK)
    String performers(@RequestParam(name = "page", defaultValue = "1") String page, Model model) {
        final int numPage = transformPageNum(page);
        final int pageSize = 10;
        Mono<Page<PerformerResponse>> performerPage = performerManagerService.getPerformerPage(numPage, pageSize);
        model.addAttribute("page", performerPage.block());
        model.addAttribute("performerName", "");
        return "performers";
    }

    @GetMapping("/performer/{performerName}")
    @ResponseStatus(HttpStatus.OK)
    String performerView(@PathVariable(name = "performerName") String performerName, Model model) {
        Mono<Set<PerformerVersionResponse>> allVersionsByName = performerManagerService.getAllVersionsByName(performerName);
        String description = performerManagerService.getDescription(performerName);
        model.addAttribute("performerName", performerName);
        model.addAttribute("performerDescription", description);
        model.addAttribute("versions", allVersionsByName.block());
        return "performer-view";
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
