package com.example.assessment.controller;

import com.example.assessment.service.LlmService;
import jakarta.annotation.Nullable;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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

    @PostMapping("/upload")
    public String upload(
            @RequestParam("pdf1") MultipartFile pdf1,
            @RequestParam("pdf2") MultipartFile pdf2,
            Model model) {

        var res = llmService.GetResult(pdf1, pdf2);
        if (!res.isOk()) {
            model.addAttribute("message", "500 Internal Server Error (" + res.unwrapErr() + ")");
            model.addAttribute("response", "{}");
            return "index";
        }

        model.addAttribute("filename1", pdf1.getOriginalFilename());
        model.addAttribute("text1", extractText(pdf1));
        model.addAttribute("filename2", pdf2.getOriginalFilename());
        model.addAttribute("text2", extractText(pdf2));

        model.addAttribute("message", "Files uploaded and processed successfully");
        model.addAttribute("response", res.unwrap().toString());
        return "index";
    }

    private String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
