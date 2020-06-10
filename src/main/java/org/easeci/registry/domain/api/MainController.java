package org.easeci.registry.domain.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@RequestMapping("/")
class MainController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String home() {
        return "home";
    }

    @GetMapping("/development")
    @ResponseStatus(HttpStatus.OK)
    String development() {
        return "development";
    }
}
