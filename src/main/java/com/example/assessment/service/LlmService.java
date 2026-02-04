package com.example.assessment.service;

import com.example.assessment.utils.Result.Err;
import com.example.assessment.utils.Result.Ok;
import com.example.assessment.utils.Result.Result;
import com.example.assessment.utils.Validation.Validator;
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

@Service
public class LlmService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GeminiKeyHeader = "x-goog-api-key";
    private final String BasePromptPath = "prompt/conflict-prompt.txt";

    @Value("${gemini-api-key}")
    private String ApiKey;
    @Value("${gemini-model-api}")
    private String Api;

    public Result<JsonNode, String> GetResult(MultipartFile pdf1, MultipartFile pdf2) {
        var reqRes = BuildHttpRequest(pdf1, pdf2);
        if (!reqRes.isOk()) {
            return new Err<>(reqRes.unwrapErr());
        }

        String response;
        try {
            var res = restTemplate.exchange(
                    Api,
                    HttpMethod.POST,
                    reqRes.unwrap(),
                    String.class)
                    .getBody();
            response = GetResult(res);
        } catch (JacksonException | NullPointerException ex) {
            return new Err<>("Invalid outer LLM API response format");
        } catch (RestClientException ex) {
            return new Err<>("Could not connect to LLM API server");
        }

        var validated = Validator.Validate(response, pdf1, pdf2);
        if (validated == null) {
            return new Err<>("Invalid inner LLM API response format");
        }
        return new Ok<>(validated);
    }

    private Result<HttpEntity<String>, String> BuildHttpRequest(MultipartFile pdf1, MultipartFile pdf2) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(GeminiKeyHeader, ApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder prompt = new StringBuilder();
        prompt = AppendBasePrompt(prompt);
        if (prompt == null) {
            return new Err<>("Error reading base prompt file");
        }

        prompt = AppendPDF(prompt, pdf1);
        if (prompt == null) {
            return new Err<>("Error reading pdf1");
        }

        prompt = AppendPDF(prompt, pdf2);
        if (prompt == null) {
            return new Err<>("Error reading pdf2");
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
            return new Ok<>(new HttpEntity<>(body, headers));
        } catch (Exception e) {
            return new Err<>("Error while creating request body");
        }
    }

    @Nullable
    private StringBuilder AppendBasePrompt(StringBuilder prompt) {
        try {
            var resource = new ClassPathResource(BasePromptPath);
            prompt.append(Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8));
            return prompt;
        } catch (IOException | OutOfMemoryError | SecurityException ex) {
            return null;
        }
    }

    @Nullable
    private StringBuilder AppendPDF(StringBuilder prompt,  MultipartFile pdf) {
        try (PDDocument document = Loader.loadPDF(pdf.getBytes())) {
            String text = new PDFTextStripper().getText(document);
            prompt.append("\n").append(pdf.getOriginalFilename()).append("\n").append(text).append("\n");
        } catch (IOException e) {
            return null;
        }
        return prompt;
    }

    private String GetResult(String body) throws JacksonException, NullPointerException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        return root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asString();
    }

    // Internal DTOs for JSON structure
    private static class Wrapper {
        public Content[] contents;
    }

    private static class Content {
        public Part[] parts;
    }

    private static class Part {
        public String text;
    }
}
