package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.services.GeminiService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Chatbot extends ActionView {

    @FXML private TextArea chatArea;
    @FXML private TextField userInput;

    private GeminiService geminiService = new GeminiService(DatabaseConnection.getConnection()); // Gemini API service

    @FXML
    public void handleSend() {
        String question = userInput.getText().trim();
        if (question.isEmpty()) return;

        chatArea.appendText("Vous: " + question + "\n");

        try {
            String answer = geminiService.getAnswer(question);

            if (answer != null) {
                chatArea.appendText("🤖 Bot: " + answer + "\n");
            } else {
                chatArea.appendText("🤖 Bot: Je ne comprends pas cette requête.\n");
            }

        } catch (Exception e) {
            chatArea.appendText("⚠ Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        userInput.clear();
    }
}
