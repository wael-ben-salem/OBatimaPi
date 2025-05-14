package io.OurBatima.core.ui;

import io.OurBatima.core.services.SimpleTranslator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;

/**
 * A simple dialog for translating text
 */
public class SimpleTranslationDialog extends Dialog<Void> {

    private final String originalText;
    private final TextArea translatedTextArea;

    /**
     * Create a new translation dialog
     *
     * @param text The text to translate
     */
    public SimpleTranslationDialog(String text) {
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

        HBox languageBox = new HBox(10);
        languageBox.setAlignment(Pos.CENTER_LEFT);

        // Create language buttons
        Button frButton = new Button("Français (original)");
        frButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button enButton = new Button("Anglais (English)");
        enButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button arButton = new Button("Arabe (العربية)");
        arButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");

        languageBox.getChildren().addAll(frButton, enButton, arButton);
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
        Text statusText = new Text("Cliquez sur une langue pour traduire");
        statusText.setFill(Color.GRAY);
        grid.add(statusText, 0, 6);

        // Add button actions
        frButton.setOnAction(event -> {
            translatedTextArea.setText(originalText);
            translatedTextArea.setStyle("-fx-text-alignment: left; -fx-font-size: 14px;");
            statusText.setText("Texte original (pas de traduction)");
            statusText.setFill(Color.GREEN);
        });

        enButton.setOnAction(event -> {
            String translatedText = SimpleTranslator.translate(originalText, "en");
            translatedTextArea.setText(translatedText);
            translatedTextArea.setStyle("-fx-text-alignment: left; -fx-font-size: 14px;");
            statusText.setText("Traduit en anglais");
            statusText.setFill(Color.GREEN);
        });

        arButton.setOnAction(event -> {
            String translatedText = SimpleTranslator.translate(originalText, "ar");
            translatedTextArea.setText(translatedText);
            translatedTextArea.setStyle("-fx-text-alignment: right; -fx-font-size: 14px;");
            statusText.setText("Traduit en arabe");
            statusText.setFill(Color.GREEN);
        });

        // Add close button
        ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        getDialogPane().setContent(grid);
    }
}