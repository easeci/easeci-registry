package org.easeci.registry.domain.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.easeci.registry.domain.api.dto.FileUploadForm;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.files.PerformerManagerService;
import org.easeci.registry.domain.files.RegistryStatus;
import org.easeci.registry.domain.files.dto.PerformerResponse;
import org.easeci.registry.domain.files.dto.PerformerVersionResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.easeci.registry.domain.api.utils.PageUtils.transformPageNum;

@Controller
@RequestMapping("/")
class RegistryController {
    private PerformerManagerService performerManagerService;

    RegistryController(PerformerManagerService performerManagerService) {
        this.performerManagerService = performerManagerService;
    }

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

    @GetMapping("/performer/upload")
    @ResponseStatus(HttpStatus.OK)
    String formView(Model model) {
        model.addAttribute("request", new FileUploadForm());
        model.addAttribute("isDataAdded", "false");
        model.addAttribute("file", new Object());
        return "upload-view";
    }

    private FileUploadForm request = null;
    @PostMapping("/performer/upload")
    @ResponseStatus(HttpStatus.OK)
    String upload(@Valid @ModelAttribute("request") FileUploadForm request,
                  @ModelAttribute("file") MultipartFile file,
                  ModelMap model, BindingResult bindingResult) throws IOException {
        if (!bindingResult.hasErrors()) {
            model.addAttribute("isDataAdded", "true");
        }
        if (nonNull(file)) {
            FileUploadRequest build = FileUploadRequest.builder()
                    .multipartFile(file.getBytes())
                    .authorFullname(this.request.getAuthorFullname())
                    .authorEmail(this.request.getAuthorEmail())
                    .performerName(this.request.getPerformerName())
                    .performerVersion(this.request.getPerformerVersion())
                    .company(this.request.getCompany())
                    .build();
            performerManagerService.uploadProcess(build)
                    .map(FileUploadResponse::getStatus)
                    .subscribe(registryStatus -> {
                        if (nonNull(registryStatus) && registryStatus.equals(RegistryStatus.SAVED)) {
                            model.addAttribute("isFileSaved", "true");
                            model.addAttribute("performerName", this.request.getPerformerName());
                        }
                    });
        } else {
            this.request = request;
        }
        return "upload-view";
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
