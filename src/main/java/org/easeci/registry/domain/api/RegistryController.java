package org.easeci.registry.domain.api;

import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.FileUploadForm;
import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.api.dto.FileUploadResponse;
import org.easeci.registry.domain.api.dto.PluginDescriptionRequest;
import org.easeci.registry.domain.files.PerformerManagerService;
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

@Slf4j
@Controller
@RequestMapping("/")
class RegistryController {
    private PerformerManagerService performerManagerService;
    private FileUploadForm request = null;

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

    @PostMapping("/performer/upload")
    @ResponseStatus(HttpStatus.OK)
    String upload(@Valid @ModelAttribute("request") FileUploadForm request,
                  @ModelAttribute("file") MultipartFile file,
                  ModelMap model, BindingResult bindingResult) throws IOException {
        if (!bindingResult.hasErrors()) {
            model.addAttribute("isDataAdded", "true");
        }
        if (nonNull(file)) {
            FileUploadRequest fileUploadRequest = prepare(file);
            FileUploadResponse fileUploadResponse = performerManagerService.uploadProcess(fileUploadRequest);
            model.addAttribute("isFileSaved", String.valueOf(fileUploadResponse.isUploaded()));
            model.addAttribute("performerName", this.request.getPerformerName());
            model.addAttribute("performerVersion", this.request.getPerformerVersion());
            model.addAttribute("validationErrors", fileUploadResponse.getValidationErrorList());
            model.addAttribute("description", new PluginDescriptionRequest(this.request.getPerformerName()));
            model.addAttribute("isDescriptionAdded", "false");
            log.info("Uploading file completed, isFileSaved={}", fileUploadResponse.isUploaded());
        } else {
            this.request = request;
            model.addAttribute("request", request);
        }
        return "upload-view";
    }

    @PostMapping("/performer/description")
    @ResponseStatus(HttpStatus.OK)
    String addDescription(@ModelAttribute("description") PluginDescriptionRequest description, ModelMap model)  {
        model.addAttribute("isDescriptionAdded", "true");
        model.addAttribute("isFileSaved", "true");
        performerManagerService.saveDescription(description.getPerformerName(), description.getDescription());
        return "upload-view";
    }

    private FileUploadRequest prepare(MultipartFile file) throws IOException {
        return  FileUploadRequest.builder()
                .multipartFile(file.getBytes())
                .authorFullname(this.request.getAuthorFullname())
                .authorEmail(this.request.getAuthorEmail())
                .performerName(this.request.getPerformerName())
                .performerVersion(this.request.getPerformerVersion())
                .company(this.request.getCompany())
                .build();
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
