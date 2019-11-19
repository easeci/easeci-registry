package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import org.easeci.registry.domain.files.FileRepresentation;
import org.easeci.registry.domain.files.FilesFacadeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static org.easeci.registry.domain.api.utils.PageUtils.transformPageNum;

@Controller
@AllArgsConstructor
@RequestMapping("/")
class RegistryController {
    private FilesFacadeService filesFacadeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String home() {
        return "home";
    }

    @GetMapping("/performers")
    @ResponseStatus(HttpStatus.OK)
    String performers(@ModelAttribute(name = "page") String page, Model model) {
        int numPage = transformPageNum(page);
        Collection<FileRepresentation.FileMeta> performers = filesFacadeService.findByPage(numPage).getPaginatedCollection();
        model.addAttribute("performers", performers);
        model.addAttribute("performerName", "");
        return "performers";
    }

    @PostMapping("/view")
    @ResponseStatus(HttpStatus.OK)
    String performerView(@ModelAttribute(name = "performerName") String performerName, Model model) {
        List<FileRepresentation.FileMeta> allVersionsByName = filesFacadeService.getAllVersionsByName(performerName);
        String description = filesFacadeService.getDescription(performerName);
        model.addAttribute("performerName", performerName);
        model.addAttribute("performerDescription", description);
        model.addAttribute("versions", allVersionsByName);
        return "performer-view";
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
