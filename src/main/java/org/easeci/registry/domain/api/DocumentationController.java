package org.easeci.registry.domain.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@RequestMapping("/documentation")
class DocumentationController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String documentation(Model model) {
        model.addAttribute("article", "introduction");
        return "documentation/documentation";
    }

    @GetMapping("/{article}")
    @ResponseStatus(HttpStatus.OK)
    String documentationArticle(@PathVariable("article") String article, Model model) {
        model.addAttribute("article", article);
        return "documentation/documentation";
    }
}
