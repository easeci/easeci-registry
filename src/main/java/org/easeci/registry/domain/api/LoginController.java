package org.easeci.registry.domain.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.api.dto.LoginRequest;
import org.easeci.registry.domain.user.dto.RegistrationRequest;
import org.easeci.registry.domain.user.dto.RegistrationResponse;
import org.easeci.registry.domain.user.RegistrationService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static java.util.Objects.nonNull;

@Slf4j
@Controller
@AllArgsConstructor
class LoginController {
    private RegistrationService registrationService;

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (nonNull(error))
            model.addAttribute("loginError", true);
        model.addAttribute("login", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    String login(@Valid @ModelAttribute("login") LoginRequest loginRequest, BindingResult bindingResult, Model model) {
        return "home";
    }

    @GetMapping("/user/details")
    @ResponseStatus(HttpStatus.OK)
    String userDetails(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "user-details";
    }

    @GetMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    String registration(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    String registration(@Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        RegistrationResponse registrationResponse = registrationService.register(registrationRequest);
        model.addAttribute("registrationResponse", registrationResponse);
        model.addAttribute("email", registrationRequest.getEmail());
        return "registration";
    }

    @GetMapping("/activation")
    @ResponseStatus(HttpStatus.OK)
    String activationLink(@Param("activationToken") String activationToken, Model model) {
        RegistrationResponse registrationResponse = registrationService.activate(activationToken);
        model.addAttribute("registrationResponse", registrationResponse);
        return "activation";
    }
}
