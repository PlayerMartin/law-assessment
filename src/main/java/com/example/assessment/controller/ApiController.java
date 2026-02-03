package com.example.assessment.controller;

import com.example.assessment.service.LlmService;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * PARALLEL: This is a standard MVC Controller.
 * 
 * - @Controller: Indicates this class handles web requests and returns Views
 * (HTML).
 * Equivalent to inheriting from `Controller` in ASP.NET MVC.
 * 
 * Differences:
 * - In C#, actions return `IActionResult`. In Spring, they usually return
 * `String` (the view name).
 * - `Model model`: Similar to `ViewBag` or passing a model object to
 * `View(model)`.
 */
@Controller
public class ApiController {

    private final LlmService llmService;

    public ApiController(LlmService llmService) {
        this.llmService = llmService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/")
    public String upload(
            @RequestParam("pdf1") MultipartFile pdf1,
            @RequestParam("pdf2") MultipartFile pdf2,
            Model model) {

        System.out.println("Processing: " + pdf1.getOriginalFilename() + pdf2.getOriginalFilename());

        String response = llmService.GetResult(pdf1, pdf2);

        String prettyResponse = GetPrettyResponse(response);
        if (prettyResponse == null) {
            model.addAttribute("message", "500 Internal Server Error (LLM response format)");
            return "index";
        }

        model.addAttribute("response", prettyResponse);
        return "index";
    }

    private String GetPrettyResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node;
        try {
            node = mapper.readTree(response);
        } catch (JacksonException ex) {
            return null;

        }

        String prettyJson;
        try {
            prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JacksonException e) {
            prettyJson = node.toString();
        }

        return prettyJson;
    }
}
