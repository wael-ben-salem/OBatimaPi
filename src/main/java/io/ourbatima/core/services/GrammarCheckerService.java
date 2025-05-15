package io.ourbatima.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service for checking grammar using RapidAPI's AI Grammar Checker
 */
public class GrammarCheckerService {
    private static final String API_URL = "https://ai-grammar-checker-i-gpt.p.rapidapi.com/api/v1/correctAndRephrase";
    private static final String API_KEY = "785640fb0emsh4c5ac04753793dp1c7232jsnf8323dfb6bbd";
    private static final String API_HOST = "ai-grammar-checker-i-gpt.p.rapidapi.com";

    // Reduced timeout to fail faster if the API is slow
    private static final int TIMEOUT_SECONDS = 8;

    private final OkHttpClient client;
    private final Gson gson;

    public GrammarCheckerService() {
        // Create HTTP client with reduced timeout
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();
    }

    /**
     * Check grammar and get corrections for the provided text
     *
     * @param text The text to check
     * @return A map containing the check results
     */
    public Map<String, Object> checkGrammar(String text) {
        Map<String, Object> result = new HashMap<>();

        if (text == null || text.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Aucun texte fourni");
            return result;
        }

        try {
            // Build the URL with query parameters
            HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                    .addQueryParameter("text", text)
                    .build();

            // Create the request with headers
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Key", API_KEY)
                    .addHeader("X-RapidAPI-Host", API_HOST)
                    .build();

            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                int statusCode = response.code();

                if (statusCode == 200 && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonResponse.has("correctedText")) {
                        String correctedText = jsonResponse.get("correctedText").getAsString();

                        if (!correctedText.equals(text)) {
                            // Calculate similarity (simplified)
                            double similarity = calculateSimilarity(text, correctedText);

                            List<Map<String, Object>> details = new ArrayList<>();
                            Map<String, Object> correction = new HashMap<>();
                            correction.put("id", "rapidapi_correction");
                            correction.put("offset", 0);
                            correction.put("length", text.length());
                            correction.put("description", "Correction grammaticale et reformulation");
                            correction.put("bad", text);
                            correction.put("better", new String[]{correctedText});
                            details.add(correction);

                            result.put("success", true);
                            result.put("original", text);
                            result.put("corrected", correctedText);
                            result.put("errors", 1);
                            result.put("details", details);
                            result.put("similarity", Math.round(similarity) + "%");
                        } else {
                            result.put("success", true);
                            result.put("original", text);
                            result.put("corrected", text);
                            result.put("errors", 0);
                            result.put("details", new ArrayList<>());
                        }
                    } else {
                        result.put("success", false);
                        result.put("message", "Format de réponse inattendu de l'API");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "Erreur lors de la vérification (Code: " + statusCode + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("API call failed: " + e.getMessage());
            // Use fallback method instead
            return getFallbackGrammarCheck(text);
        }

        return result;
    }

    /**
     * Calculate similarity between two strings (percentage)
     *
     * @param s1 First string
     * @param s2 Second string
     * @return Similarity percentage
     */
    private double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 100.0;

        int distance = levenshteinDistance(s1, s2);
        return (1.0 - (double) distance / maxLength) * 100.0;
    }

    /**
     * Calculate Levenshtein distance between two strings
     *
     * @param s1 First string
     * @param s2 Second string
     * @return Levenshtein distance
     */
    /**
     * Fallback grammar check method that works offline
     * This is used when the API call fails or times out
     *
     * @param text The text to check
     * @return A map containing the check results
     */
    private Map<String, Object> getFallbackGrammarCheck(String text) {
        Map<String, Object> result = new HashMap<>();

        // Simple corrections for common French grammar mistakes
        String correctedText = text;
        List<Map<String, Object>> details = new ArrayList<>();

        // Common corrections (very simplified)
        String[][] commonErrors = {
                {"a", "à"}, // Missing accent
                {"ou", "où"}, // Missing accent
                {"ca", "ça"}, // Missing cedilla
                {"cest", "c'est"}, // Missing apostrophe
                {"sest", "s'est"}, // Missing apostrophe
                {"jai", "j'ai"}, // Missing apostrophe
                {"  ", " "}, // Double spaces
                {".", ". "}, // Missing space after period
                {",", ", "}, // Missing space after comma
                {"!", "! "}, // Missing space after exclamation
                {"?", "? "}, // Missing space after question mark
                {";", "; "}, // Missing space after semicolon
                {":", ": "}, // Missing space after colon
        };

        // Apply corrections
        for (String[] error : commonErrors) {
            String pattern = error[0];
            String replacement = error[1];

            if (text.contains(pattern)) {
                correctedText = correctedText.replace(pattern, replacement);

                // Add to details
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", "fallback_" + pattern);
                detail.put("offset", text.indexOf(pattern));
                detail.put("length", pattern.length());
                detail.put("description", "Correction automatique");
                detail.put("bad", pattern);
                detail.put("better", new String[]{replacement});
                details.add(detail);
            }
        }

        // Capitalize first letter of sentences
        if (!text.isEmpty() && Character.isLowerCase(text.charAt(0))) {
            correctedText = Character.toUpperCase(text.charAt(0)) + correctedText.substring(1);

            Map<String, Object> detail = new HashMap<>();
            detail.put("id", "fallback_capitalize");
            detail.put("offset", 0);
            detail.put("length", 1);
            detail.put("description", "Majuscule en début de phrase");
            detail.put("bad", text.substring(0, 1));
            detail.put("better", new String[]{correctedText.substring(0, 1)});
            details.add(detail);
        }

        // Check if any corrections were made
        if (!correctedText.equals(text)) {
            double similarity = calculateSimilarity(text, correctedText);

            result.put("success", true);
            result.put("original", text);
            result.put("corrected", correctedText);
            result.put("errors", details.size());
            result.put("details", details);
            result.put("similarity", Math.round(similarity) + "%");
            result.put("fallback", true);
            result.put("message", "Vérification hors ligne (API indisponible)");
        } else {
            result.put("success", true);
            result.put("original", text);
            result.put("corrected", text);
            result.put("errors", 0);
            result.put("details", new ArrayList<>());
            result.put("fallback", true);
            result.put("message", "Vérification hors ligne (API indisponible). Aucune erreur trouvée.");
        }

        return result;
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }
}