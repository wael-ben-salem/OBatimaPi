package io.OurBatima.core.ui;

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
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interactive dialog for displaying grammar check results with clickable corrections
 */
public class InteractiveGrammarCheckDialog extends Dialog<String> {

    private final String originalText;
    private String correctedText;
    private final TextArea resultTextArea;

    /**
     * Create a new interactive dialog to display grammar check results
     *
     * @param results The results from the grammar checker
     */
    public InteractiveGrammarCheckDialog(Map<String, Object> results) {
        setTitle("Vérification grammaticale interactive");
        setHeaderText("Analyse grammaticale");
        initModality(Modality.APPLICATION_MODAL);

        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        boolean success = (boolean) results.get("success");

        // Initialize the result text area
        resultTextArea = new TextArea();
        resultTextArea.setWrapText(true);
        resultTextArea.setPrefHeight(150);
        resultTextArea.setPrefWidth(500);

        if (success) {
            // Get the original and corrected text
            originalText = (String) results.get("original");
            correctedText = (String) results.get("corrected");
            int errors = ((Number) results.get("errors")).intValue();

            // Add original text section
            Text originalLabel = new Text("Texte original:");
            originalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            grid.add(originalLabel, 0, 0);

            TextArea originalTextArea = new TextArea(originalText);
            originalTextArea.setEditable(false);
            originalTextArea.setWrapText(true);
            originalTextArea.setPrefHeight(100);
            originalTextArea.setPrefWidth(500);
            grid.add(originalTextArea, 0, 1);

            // Initialize result text area with original text
            resultTextArea.setText(originalText);

            if (errors > 0) {
                // Add interactive corrections section
                Text correctionsLabel = new Text("Corrections suggérées (cliquez pour appliquer):");
                correctionsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                grid.add(correctionsLabel, 0, 2);

                VBox correctionsBox = new VBox(10);

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> details = results.containsKey("details")
                        ? (List<Map<String, Object>>) results.get("details")
                        : new ArrayList<>();

                if (!details.isEmpty()) {
                    for (Map<String, Object> detail : details) {
                        String description = (String) detail.get("description");
                        String bad = (String) detail.get("bad");
                        @SuppressWarnings("unchecked")
                        String[] better = (String[]) detail.get("better");

                        HBox correctionBox = new HBox(10);
                        correctionBox.setAlignment(Pos.CENTER_LEFT);
                        correctionBox.setPadding(new Insets(5));
                        correctionBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f8f8f8; -fx-background-radius: 5;");

                        Label descLabel = new Label(description + ":");
                        descLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

                        Label badLabel = new Label("\"" + bad + "\"");
                        badLabel.setTextFill(Color.RED);

                        Label arrowLabel = new Label(" → ");

                        Button applyButton = new Button("\"" + better[0] + "\"");
                        applyButton.setTextFill(Color.GREEN);
                        applyButton.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 3;");

                        // Add click handler to apply the correction
                        applyButton.setOnAction(event -> {
                            String currentText = resultTextArea.getText();
                            String newText = currentText.replace(bad, better[0]);
                            resultTextArea.setText(newText);
                            applyButton.setDisable(true);
                            applyButton.setText("✓ Appliqué");
                        });

                        correctionBox.getChildren().addAll(descLabel, badLabel, arrowLabel, applyButton);
                        correctionsBox.getChildren().add(correctionBox);
                    }
                } else if (!originalText.equals(correctedText)) {
                    // If we have a corrected text but no details, create a single correction
                    HBox correctionBox = new HBox(10);
                    correctionBox.setAlignment(Pos.CENTER_LEFT);
                    correctionBox.setPadding(new Insets(5));
                    correctionBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f8f8f8; -fx-background-radius: 5;");

                    Label descLabel = new Label("Correction complète:");
                    descLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

                    Button applyButton = new Button("Appliquer la correction suggérée");
                    applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                    // Add click handler to apply the full correction
                    applyButton.setOnAction(event -> {
                        resultTextArea.setText(correctedText);
                        applyButton.setDisable(true);
                        applyButton.setText("✓ Appliqué");
                    });

                    correctionBox.getChildren().addAll(descLabel, applyButton);
                    correctionsBox.getChildren().add(correctionBox);
                }

                ScrollPane scrollPane = new ScrollPane(correctionsBox);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(150);
                grid.add(scrollPane, 0, 3);

                // Add summary
                Text summaryText = new Text(errors + " erreur(s) trouvée(s)");
                if (results.containsKey("similarity")) {
                    summaryText.setText(summaryText.getText() + " - Similarité: " + results.get("similarity"));
                }
                summaryText.setFill(Color.RED);
                summaryText.setFont(Font.font("System", FontWeight.BOLD, 12));
                grid.add(summaryText, 0, 4);

            } else {
                // No errors found
                Text noErrorsText = new Text("Aucune erreur trouvée. Le texte est grammaticalement correct.");
                noErrorsText.setFill(Color.GREEN);
                noErrorsText.setFont(Font.font("System", FontWeight.BOLD, 14));
                grid.add(noErrorsText, 0, 2);
            }

        } else {
            // Display error message
            originalText = "";
            correctedText = "";

            String message = (String) results.get("message");
            Label errorLabel = new Label("Erreur: " + message);
            errorLabel.setTextFill(Color.RED);
            grid.add(errorLabel, 0, 0);
        }

        // Add result text area section
        Text resultLabel = new Text("Texte final (éditable):");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(resultLabel, 0, 5);

        grid.add(resultTextArea, 0, 6);

        // Add buttons
        ButtonType applyButtonType = new ButtonType("Appliquer les corrections", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = ButtonType.CANCEL;

        getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        // Set the result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                return resultTextArea.getText();
            }
            return null;
        });

        getDialogPane().setContent(grid);
    }
}