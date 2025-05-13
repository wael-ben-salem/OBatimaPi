package io.OurBatima.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service for translating text using MyMemory Translation API
 */
public class TranslationService {
    private static final String API_URL = "https://api.mymemory.translated.net/get";
    private static final String EMAIL = "anonymous@user.com";
    private static final int TIMEOUT_SECONDS = 10;
    
    private final OkHttpClient client;
    private final Gson gson;
    
    // Fallback translations for when the API fails
    private static final Map<String, Map<String, String>> FALLBACK_TRANSLATIONS = new HashMap<>();
    
    static {
        // English fallback translations
        Map<String, String> enTranslations = new HashMap<>();
        enTranslations.put("Bonjour", "Hello");
        enTranslations.put("Merci", "Thank you");
        enTranslations.put("Problème", "Problem");
        enTranslations.put("Réclamation", "Claim");
        enTranslations.put("Réponse", "Response");
        enTranslations.put("Nous avons bien reçu votre réclamation", "We have received your claim");
        enTranslations.put("Nous sommes désolés pour le désagrément", "We are sorry for the inconvenience");
        enTranslations.put("Votre problème sera résolu sous peu", "Your issue will be resolved shortly");
        enTranslations.put("Merci pour votre patience", "Thank you for your patience");
        enTranslations.put("Cordialement", "Best regards");
        FALLBACK_TRANSLATIONS.put("en", enTranslations);
        
        // Arabic fallback translations
        Map<String, String> arTranslations = new HashMap<>();
        arTranslations.put("Bonjour", "مرحبا");
        arTranslations.put("Merci", "شكرا");
        arTranslations.put("Problème", "مشكلة");
        arTranslations.put("Réclamation", "شكوى");
        arTranslations.put("Réponse", "رد");
        arTranslations.put("Nous avons bien reçu votre réclamation", "لقد تلقينا شكواك");
        arTranslations.put("Nous sommes désolés pour le désagrément", "نأسف للإزعاج");
        arTranslations.put("Votre problème sera résolu sous peu", "سيتم حل مشكلتك قريبًا");
        arTranslations.put("Merci pour votre patience", "شكرا لصبرك");
        arTranslations.put("Cordialement", "مع أطيب التحيات");
        FALLBACK_TRANSLATIONS.put("ar", arTranslations);
    }
    
    public TranslationService() {
        // Create HTTP client with timeout
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        
        this.gson = new Gson();
    }
    
    /**
     * Translate text from French to the target language
     * 
     * @param text The text to translate
     * @param targetLang The target language code (en, ar)
     * @return A map containing the translation results
     */
    public Map<String, Object> translate(String text, String targetLang) {
        Map<String, Object> result = new HashMap<>();
        result.put("originalText", text);
        result.put("targetLanguage", targetLang);
        
        // If target language is not supported or is "original", return the original text
        if (targetLang == null || targetLang.equals("original") || 
                (!targetLang.equals("en") && !targetLang.equals("ar"))) {
            result.put("translatedText", text);
            result.put("success", true);
            result.put("message", "Texte original (pas de traduction demandée)");
            return result;
        }
        
        try {
            // Build the URL with query parameters
            HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                    .addQueryParameter("q", text)
                    .addQueryParameter("langpair", "fr|" + targetLang)
                    .addQueryParameter("de", EMAIL)
                    .build();
            
            // Create the request
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            
            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                int statusCode = response.code();
                
                if (statusCode == 200 && response.body() != null) {
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    
                    if (jsonResponse.has("responseData") && 
                            jsonResponse.getAsJsonObject("responseData").has("translatedText")) {
                        String translatedText = jsonResponse.getAsJsonObject("responseData")
                                .get("translatedText").getAsString();
                        
                        // Check match quality if available
                        if (jsonResponse.getAsJsonObject("responseData").has("match")) {
                            double match = jsonResponse.getAsJsonObject("responseData")
                                    .get("match").getAsDouble();
                            
                            if (match < 0.5) {
                                translatedText += "\n\n(Note: Cette traduction peut ne pas être précise à 100%)";
                            }
                        }
                        
                        result.put("translatedText", translatedText);
                        result.put("success", true);
                        result.put("message", "Traduction réussie");
                    } else {
                        // Use fallback translation
                        String fallbackText = getFallbackTranslation(text, targetLang);
                        result.put("translatedText", fallbackText);
                        result.put("success", true);
                        result.put("message", "La traduction API a échoué. Utilisation de la traduction de secours.");
                    }
                } else {
                    // Use fallback translation
                    String fallbackText = getFallbackTranslation(text, targetLang);
                    result.put("translatedText", fallbackText);
                    result.put("success", true);
                    result.put("message", "Erreur API (Code: " + statusCode + "). Utilisation de la traduction de secours.");
                }
            }
        } catch (IOException e) {
            // Use fallback translation
            String fallbackText = getFallbackTranslation(text, targetLang);
            result.put("translatedText", fallbackText);
            result.put("success", true);
            result.put("message", "Erreur lors de la traduction: " + e.getMessage() + ". Utilisation de la traduction de secours.");
        }
        
        return result;
    }
    
    /**
     * Get a fallback translation using the simple dictionary
     * 
     * @param text The text to translate
     * @param targetLang The target language code
     * @return The translated text or the original if no translation is available
     */
    private String getFallbackTranslation(String text, String targetLang) {
        if (FALLBACK_TRANSLATIONS.containsKey(targetLang)) {
            String translatedText = text;
            Map<String, String> translations = FALLBACK_TRANSLATIONS.get(targetLang);
            
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                translatedText = translatedText.replace(entry.getKey(), entry.getValue());
            }
            
            if (!translatedText.equals(text)) {
                return translatedText + "\n\n(Note: Traduction partielle basée sur un dictionnaire simple)";
            }
        }
        
        return text + "\n\n(Désolé, la traduction n'est pas disponible pour le moment)";
    }
}
