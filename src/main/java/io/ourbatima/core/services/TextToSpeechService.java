package io.ourbatima.core.services;

import javax.sound.sampled.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextToSpeechService {
    private static final String API_KEY = "9eea432db682425082746758304f232c"; // Replace with your actual API key
    private static final String API_URL = "https://api.voicerss.org/";

    public static void speak(String text) {
        try {
            // Construct the API request URL
            String requestUrl = API_URL + "?key=" + API_KEY + "&hl=fr-fr&c=WAV&f=16khz_16bit_mono&src=" +
                    text.replace(" ", "%20");

            // Make the API call
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setRequestMethod("GET");

            // Read the response (audio file)
            try (InputStream in = conn.getInputStream()) {
                byte[] audioData = in.readAllBytes();
                playAudio(audioData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playAudio(byte[] audioData) throws Exception {
        File tempFile = File.createTempFile("tts", ".wav");
        Files.write(tempFile.toPath(), audioData);

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(tempFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000); // Wait until the audio finishes
        }
        tempFile.delete(); // Clean up temp file
    }
}
