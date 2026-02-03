package com.example.assessment.service;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * PARALLEL: This is your Business Logic Layer.
 * 
 * - @Service: Registers this class in the Dependency Injection container (IOC).
 * Equivalent to `services.AddScoped<TaskService>();` in C# Startup.cs /
 * Program.cs.
 */
@Service
public class LlmService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GeminiKeyHeader = "x-goog-api-key";

    @Value("${gemini-api-key}")
    private String ApiKey;
    @Value("${gemini-model-api}")
    private String Api;

    public String GetResult(MultipartFile pdf1, MultipartFile pdf2) {
        var entity = BuildHttpRequest(pdf1, pdf2);

        if (entity == null) {
            return "500 Internal Server Error (Prompt reading error)";
        }

        try {
            String res = restTemplate.exchange(
                    Api,
                    HttpMethod.POST,
                    entity,
                    String.class).getBody();

            return GetResult(res);
        } catch (JacksonException | RestClientException ex) {
            return ex.getMessage();
        }
    }

    @Nullable
    private HttpEntity<String> BuildHttpRequest(MultipartFile pdf1, MultipartFile pdf2) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GeminiKeyHeader, ApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder prompt = new StringBuilder();

        try {
            var resource = new ClassPathResource("prompt/conflict-prompt.txt");
            prompt.append(Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8));
        } catch (IOException | OutOfMemoryError | SecurityException ex) {
            return null;
        }

        try (PDDocument document = Loader.loadPDF(pdf1.getBytes())) {
            String text = new PDFTextStripper().getText(document);
            prompt.append("\n").append(pdf1.getOriginalFilename()).append("\n").append(text).append("\n");
        } catch (IOException e) {
            return null;
        }

        try (PDDocument document = Loader.loadPDF(pdf2.getBytes())) {
            String text = new PDFTextStripper().getText(document);
            prompt.append("\n").append(pdf2.getOriginalFilename()).append("\n").append(text).append("\n");
        } catch (IOException e) {
            return null;
        }

        // Use ObjectMapper to safely escape the prompt text into valid JSON
        ObjectMapper mapper = new ObjectMapper();
        Wrapper wrapper = new Wrapper();
        Part part = new Part();
        part.text = prompt.toString();
        Content content = new Content();
        content.parts = new Part[] { part };
        wrapper.contents = new Content[] { content };

        try {
            String body = mapper.writeValueAsString(wrapper);
            return new HttpEntity<>(body, headers);
        } catch (Exception e) {
            return null;
        }
    }

    private String GetResult(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asString();
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    // Internal DTOs for JSON structure
    public static class Wrapper {
        public Content[] contents;
    }

    public static class Content {
        public Part[] parts;
    }

    public static class Part {
        public String text;
    }
}
