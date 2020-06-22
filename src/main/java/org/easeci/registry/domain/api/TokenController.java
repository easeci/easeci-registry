package org.easeci.registry.domain.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.token.UploadTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/token")
class TokenController {
    private UploadTokenService uploadTokenService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String getToken(Model model) {
        model.addAttribute("requestedForToken", false);
        return "token-view";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    String getTokenDisplay(Model model, Principal principal) {
        uploadTokenService.getToken()
                .ifPresentOrElse(uploadTokenDto -> {
                    model.addAttribute("token", uploadTokenDto);
                    model.addAttribute("requestedForToken", true);
                    CompletableFuture.runAsync(() -> uploadTokenService.reserveToken(uploadTokenDto.getId(), principal));
                }, () -> model.addAttribute("noTokenFound", true));
        return "token-view";
    }
}
