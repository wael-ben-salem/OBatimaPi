package io.ourbatima.core.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class GeminiAPI {
    private static final String API_KEY = "AIzaSyCwoIf6m-Eyf-iK4nYlbT6kGW6MIrJnEgA";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="+ API_KEY;

    public String getEstimation(String styleArch, String superficie, String emplacement, String type) {
        try {
            String json = String.format(
                    "{\"contents\": [{\"role\": \"user\", \"parts\": [{\"text\": \"Estimate the cost and duration for a project with the following details: " +
                            "Style Architecture: %s, Superficie: %s, Emplacement: %s, Type: %s. Please respond in French.\"}]}]}",
                    styleArch, superficie, emplacement, type
            );

            // Create HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            // Send request and handle response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Error: " + response.statusCode() + " - " + response.body();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}