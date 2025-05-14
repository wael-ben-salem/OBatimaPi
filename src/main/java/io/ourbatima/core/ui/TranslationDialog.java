package io.ourbatima.core.ui;

import io.OurBatima.core.services.TranslationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.util.Map;

/**
 * Dialog for displaying translation results
 */
public class TranslationDialog extends Dialog<Void> {

    private final TranslationService translationService;
    private final String originalText;
    private TextArea translatedTextArea;
    private ComboBox<String> languageComboBox;

    /**
     * Create a new translation dialog
     *
     * @param text The text to translate
     */
    public TranslationDialog(String text) {
        this.translationService = new TranslationService();
        this.originalText = text;

        setTitle("Traduction");
        setHeaderText("Traduire le texte");
        initModality(Modality.APPLICATION_MODAL);

        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        // Add original text section
        Text originalLabel = new Text("Texte original (Français):");
        originalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(originalLabel, 0, 0);

        TextArea originalTextArea = new TextArea(originalText);
        originalTextArea.setEditable(false);
        originalTextArea.setWrapText(true);
        originalTextArea.setPrefHeight(100);
        originalTextArea.setPrefWidth(500);
        grid.add(originalTextArea, 0, 1);

        // Add language selection
        Text languageLabel = new Text("Langue cible:");
        languageLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(languageLabel, 0, 2);

        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("original", "en", "ar");
        languageComboBox.setValue("original");
        languageComboBox.setPromptText("Sélectionnez une langue");

        // Add language names for clarity
        languageComboBox.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case "original":
                            setText("Français (original)");
                            break;
                        case "en":
                            setText("Anglais (English)");
                            break;
                        case "ar":
                            setText("Arabe (العربية)");
                            break;
                        default:
                            setText(item);
                    }
                }
            }
        });

        // Use the same cell factory for the button
        languageComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case "original":
                            setText("Français (original)");
                            break;
                        case "en":
                            setText("Anglais (English)");
                            break;
                        case "ar":
                            setText("Arabe (العربية)");
                            break;
                        default:
                            setText(item);
                    }
                }
            }
        });

        Button translateButton = new Button("Traduire");
        translateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox languageBox = new HBox(10);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        languageBox.getChildren().addAll(languageComboBox, translateButton);
        grid.add(languageBox, 0, 3);

        // Add translated text section
        Text translatedLabel = new Text("Texte traduit:");
        translatedLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(translatedLabel, 0, 4);

        translatedTextArea = new TextArea(originalText);
        translatedTextArea.setEditable(false);
        translatedTextArea.setWrapText(true);
        translatedTextArea.setPrefHeight(150);
        translatedTextArea.setPrefWidth(500);
        grid.add(translatedTextArea, 0, 5);

        // Add status message
        Text statusText = new Text("Sélectionnez une langue et cliquez sur Traduire");
        statusText.setFill(Color.GRAY);
        grid.add(statusText, 0, 6);

        // Add translate button action
        translateButton.setOnAction(event -> {
            String targetLang = languageComboBox.getValue();
            statusText.setText("Traduction en cours...");
            statusText.setFill(Color.BLUE);

            // Run translation in a background thread
            Thread thread = new Thread(() -> {
                Map<String, Object> result = translationService.translate(originalText, targetLang);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    String translatedText = (String) result.get("translatedText");
                    String message = (String) result.get("message");
                    boolean success = (boolean) result.get("success");

                    translatedTextArea.setText(translatedText);
                    statusText.setText(message);
                    statusText.setFill(success ? Color.GREEN : Color.RED);

                    // Set text direction for Arabic
                    if (targetLang.equals("ar")) {
                        translatedTextArea.setStyle("-fx-text-alignment: right; -fx-font-size: 14px;");
                    } else {
                        translatedTextArea.setStyle("-fx-text-alignment: left; -fx-font-size: 14px;");
                    }
                });
            });

            thread.setDaemon(true);
            thread.start();
        });

        // Add close button
        ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        getDialogPane().setContent(grid);
    }
}