package org.easeci.registry.domain.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/")
class RegistryController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String home() {
        return "home";
    }

    @GetMapping("/performers")
    @ResponseStatus(HttpStatus.OK)
    String performers() {
        return "performers";
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
