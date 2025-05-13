package io.OurBatima.core.services;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple offline translator for French to English and Arabic
 */
public class SimpleTranslator {
    
    // Dictionaries for translations
    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();
    
    static {
        // English translations
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
        enTranslations.put("Veuillez", "Please");
        enTranslations.put("Attendre", "Wait");
        enTranslations.put("Jour", "Day");
        enTranslations.put("Semaine", "Week");
        enTranslations.put("Mois", "Month");
        enTranslations.put("Année", "Year");
        TRANSLATIONS.put("en", enTranslations);
        
        // Arabic translations
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
        arTranslations.put("Veuillez", "يرجى");
        arTranslations.put("Attendre", "انتظر");
        arTranslations.put("Jour", "يوم");
        arTranslations.put("Semaine", "أسبوع");
        arTranslations.put("Mois", "شهر");
        arTranslations.put("Année", "سنة");
        TRANSLATIONS.put("ar", arTranslations);
    }
    
    /**
     * Translate text from French to the target language
     * 
     * @param text The text to translate
     * @param targetLang The target language code (en, ar)
     * @return The translated text
     */
    public static String translate(String text, String targetLang) {
        // If target language is not supported or is "original", return the original text
        if (targetLang == null || targetLang.equals("original") || 
                (!targetLang.equals("en") && !targetLang.equals("ar"))) {
            return text;
        }
        
        // Get the translation dictionary for the target language
        Map<String, String> translations = TRANSLATIONS.get(targetLang);
        if (translations == null) {
            return text;
        }
        
        // Translate word by word
        String translatedText = text;
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            translatedText = translatedText.replace(entry.getKey(), entry.getValue());
        }
        
        // Add a note if the text was translated
        if (!translatedText.equals(text)) {
            translatedText += "\n\n(Note: Traduction partielle basée sur un dictionnaire simple)";
        } else {
            translatedText += "\n\n(Note: Aucune traduction disponible pour ce texte)";
        }
        
        return translatedText;
    }
}
