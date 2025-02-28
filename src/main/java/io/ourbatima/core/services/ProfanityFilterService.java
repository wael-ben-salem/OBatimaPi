package io.ourbatima.core.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ProfanityFilterService {
    private static final String API_KEY = "gIHHFkHnq7ZoSD46N6MG/g==jBAL5pIyTfP8XOJJ";
    private static final String API_URL = "https://api.api-ninjas.com/v1/profanityfilter?text=";

    public static String filterProfanity(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            URL url = new URL(API_URL + encodedText);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-Api-Key", API_KEY);
            connection.setRequestProperty("accept", "application/json");

            InputStream responseStream = connection.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);

            return root.path("censored").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }
}

