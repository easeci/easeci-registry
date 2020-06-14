package org.easeci.registry.domain.api;

import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
class LoginController {

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    String login(Model model) {
        model.addAttribute("login", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    String login(@Valid @ModelAttribute("login") LoginRequest loginRequest, BindingResult bindingResult, Model model) {
        return "login";
    }
}
