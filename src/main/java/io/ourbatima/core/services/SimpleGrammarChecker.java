package io.ourbatima.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple grammar checker for French text that works offline
 */
public class SimpleGrammarChecker {

    /**
     * Check grammar in the given text
     * @param text The text to check
     * @return A map with the results
     */
    public static Map<String, Object> checkGrammar(String text) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> corrections = new ArrayList<>();

        // Make a copy of the original text
        String correctedText = text;

        // Check for common French grammar mistakes
        checkMissingAccents(text, correctedText, corrections);
        checkMissingApostrophes(text, correctedText, corrections);
        checkCapitalization(text, correctedText, corrections);
        checkSpacing(text, correctedText, corrections);

        // Apply all corrections to get the final corrected text
        correctedText = applyCorrections(text, corrections);

        // Prepare the result
        result.put("original", text);
        result.put("corrected", correctedText);
        result.put("corrections", corrections);
        result.put("hasErrors", !corrections.isEmpty());
        result.put("errorCount", corrections.size());

        return result;
    }

    /**
     * Apply all corrections to the text
     * @param text The original text
     * @param corrections The list of corrections
     * @return The corrected text
     */
    private static String applyCorrections(String text, List<Map<String, Object>> corrections) {
        String result = text;

        // Sort corrections by position (descending) to avoid offset issues
        corrections.sort((a, b) -> {
            int posA = (int) a.get("position");
            int posB = (int) b.get("position");
            return Integer.compare(posB, posA);
        });

        // Apply each correction
        for (Map<String, Object> correction : corrections) {
            String original = (String) correction.get("original");
            String replacement = (String) correction.get("replacement");
            int position = (int) correction.get("position");

            // Replace the text at the specified position
            result = result.substring(0, position) +
                    replacement +
                    result.substring(position + original.length());
        }

        return result;
    }

    /**
     * Check for missing accents in common French words
     */
    private static void checkMissingAccents(String text, String correctedText, List<Map<String, Object>> corrections) {
        // Common words with accents
        String[][] accentWords = {
                {"a", "à"},
                {"ca", "ça"},
                {"ou", "où"},
                {"tres", "très"},
                {"voila", "voilà"},
                {"deja", "déjà"},
                {"ete", "été"},
                {"apres", "après"},
                {"meme", "même"},
                {"etre", "être"},
                {"theatre", "théâtre"},
                {"ecole", "école"},
                {"eleve", "élève"},
                {"cafe", "café"},
                {"cinema", "cinéma"},
                {"hotel", "hôtel"},
                {"hopital", "hôpital"},
                {"probleme", "problème"},
                {"systeme", "système"},
                {"different", "différent"},
                {"general", "général"},
                {"etudiant", "étudiant"},
                {"francais", "français"},
                {"americain", "américain"},
                {"europeen", "européen"},
                {"president", "président"},
                {"secretaire", "secrétaire"},
                {"medecin", "médecin"},
                {"telephone", "téléphone"},
                {"television", "télévision"},
                {"numero", "numéro"},
                {"premiere", "première"},
                {"deuxieme", "deuxième"},
                {"troisieme", "troisième"},
                {"quatrieme", "quatrième"},
                {"cinquieme", "cinquième"},
                {"sixieme", "sixième"},
                {"septieme", "septième"},
                {"huitieme", "huitième"},
                {"neuvieme", "neuvième"},
                {"dixieme", "dixième"}
        };

        for (String[] pair : accentWords) {
            String wordWithoutAccent = pair[0];
            String wordWithAccent = pair[1];

            // Find all occurrences of the word without accent
            Pattern pattern = Pattern.compile("\\b" + wordWithoutAccent + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int position = matcher.start();
                String found = matcher.group();

                // Create a correction
                Map<String, Object> correction = new HashMap<>();
                correction.put("type", "accent");
                correction.put("original", found);
                correction.put("replacement", wordWithAccent);
                correction.put("position", position);
                correction.put("description", "Accent manquant");

                corrections.add(correction);
            }
        }
    }

    /**
     * Check for missing apostrophes in common French contractions
     */
    private static void checkMissingApostrophes(String text, String correctedText, List<Map<String, Object>> corrections) {
        // Common contractions
        String[][] contractions = {
                {"cest", "c'est"},
                {"sest", "s'est"},
                {"jai", "j'ai"},
                {"jen", "j'en"},
                {"jy", "j'y"},
                {"quil", "qu'il"},
                {"quils", "qu'ils"},
                {"quelle", "qu'elle"},
                {"quelles", "qu'elles"},
                {"jusqua", "jusqu'à"},
                {"aujourdhui", "aujourd'hui"},
                {"lhomme", "l'homme"},
                {"lenfant", "l'enfant"},
                {"leau", "l'eau"},
                {"lami", "l'ami"},
                {"lamie", "l'amie"},
                {"lhistoire", "l'histoire"},
                {"lheure", "l'heure"},
                {"lannee", "l'année"},
                {"lecole", "l'école"}
        };

        for (String[] pair : contractions) {
            String withoutApostrophe = pair[0];
            String withApostrophe = pair[1];

            // Find all occurrences of the contraction without apostrophe
            Pattern pattern = Pattern.compile("\\b" + withoutApostrophe + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int position = matcher.start();
                String found = matcher.group();

                // Create a correction
                Map<String, Object> correction = new HashMap<>();
                correction.put("type", "apostrophe");
                correction.put("original", found);
                correction.put("replacement", withApostrophe);
                correction.put("position", position);
                correction.put("description", "Apostrophe manquante");

                corrections.add(correction);
            }
        }
    }

    /**
     * Check for capitalization at the beginning of sentences
     */
    private static void checkCapitalization(String text, String correctedText, List<Map<String, Object>> corrections) {
        // Check first character of the text
        if (!text.isEmpty() && Character.isLowerCase(text.charAt(0))) {
            Map<String, Object> correction = new HashMap<>();
            correction.put("type", "capitalization");
            correction.put("original", text.substring(0, 1));
            correction.put("replacement", text.substring(0, 1).toUpperCase());
            correction.put("position", 0);
            correction.put("description", "Majuscule en début de phrase");

            corrections.add(correction);
        }

        // Check after periods, exclamation points, and question marks
        Pattern pattern = Pattern.compile("[.!?]\\s+[a-zàáâäæçèéêëìíîïòóôöùúûüÿ]");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int position = matcher.end() - 1;
            String found = text.substring(position, position + 1);

            Map<String, Object> correction = new HashMap<>();
            correction.put("type", "capitalization");
            correction.put("original", found);
            correction.put("replacement", found.toUpperCase());
            correction.put("position", position);
            correction.put("description", "Majuscule en début de phrase");

            corrections.add(correction);
        }
    }

    /**
     * Check for proper spacing around punctuation
     */
    private static void checkSpacing(String text, String correctedText, List<Map<String, Object>> corrections) {
        // Check for missing spaces after punctuation
        Pattern pattern = Pattern.compile("([.,;:!?])([a-zA-ZàáâäæçèéêëìíîïòóôöùúûüÿÀÁÂÄÆÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜŸ])");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            int position = matcher.start();
            String found = matcher.group();
            String punctuation = matcher.group(1);
            String nextChar = matcher.group(2);

            Map<String, Object> correction = new HashMap<>();
            correction.put("type", "spacing");
            correction.put("original", found);
            correction.put("replacement", punctuation + " " + nextChar);
            correction.put("position", position);
            correction.put("description", "Espace manquant après la ponctuation");

            corrections.add(correction);
        }

        // Check for double spaces
        pattern = Pattern.compile("  +");
        matcher = pattern.matcher(text);

        while (matcher.find()) {
            int position = matcher.start();
            String found = matcher.group();

            Map<String, Object> correction = new HashMap<>();
            correction.put("type", "spacing");
            correction.put("original", found);
            correction.put("replacement", " ");
            correction.put("position", position);
            correction.put("description", "Espaces multiples");

            corrections.add(correction);
        }
    }
}