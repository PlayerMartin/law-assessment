package com.example.assessment.controller;

import com.example.assessment.service.LlmService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
public class APIController {

    private final LlmService llmService;

    public APIController(LlmService llmService) {
        this.llmService = llmService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("pdf1") MultipartFile pdf1,
            @RequestParam("pdf2") MultipartFile pdf2,
            Model model) {

        // process files

        System.out.println(pdf1.getOriginalFilename());
        System.out.println(pdf2.getOriginalFilename());

        model.addAttribute("message", "Files uploaded successfully");
        return "index";
    }
}
