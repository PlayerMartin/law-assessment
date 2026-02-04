package com.example.assessment.utils.Validation;

import jakarta.annotation.Nullable;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.regex.Pattern;

public class Validator {

    /// Validate the LLM response, must be JSON. Regex match excerpts,
    /// if any of the excerpt pair not present in the document
    /// remove the contradiction pair (LLM hallucinated).
    @Nullable
    public static JsonNode Validate(String text, MultipartFile pdf1, MultipartFile pdf2) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(text);
        } catch (JacksonException ex) {
            return null;
        }

        var contradictions = (ArrayNode) root.get("contradictions");
        if (contradictions == null)
            return null;

        try {
            for (int i = contradictions.size() - 1; i >= 0; i--) {
                JsonNode c = contradictions.get(i);
                JsonNode sections = c.get("sections");

                String excerpt1 = sections.get(0).get("text_excerpt").asString().toLowerCase();
                String excerpt2 = sections.get(1).get("text_excerpt").asString().toLowerCase();

                boolean ok1 = validateExcerpt(pdf1, excerpt1);
                boolean ok2 = validateExcerpt(pdf2, excerpt2);

                if (!ok1 || !ok2) {
                    contradictions.remove(i);
                }
            }
        } catch (IOException ex) {
            return null;
        }

        return root;
    }

    private static boolean validateExcerpt(MultipartFile pdf, String excerpt) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdf.getBytes())) {
            String text = new PDFTextStripper().getText(document).toLowerCase();

            String normText = normalize(text);
            String normExcerpt = normalize(excerpt);

            return normText.contains(normExcerpt.toLowerCase());
        }
    }

    private static String normalize(String s) {
        return s
                .replaceAll("-\\s*\\n\\s*", "")   // fix hyphen line breaks
                .replaceAll("\\s+", " ")         // collapse whitespace
                .replace('\u00A0', ' ')          // non-breaking space
                .trim();
    }
}
