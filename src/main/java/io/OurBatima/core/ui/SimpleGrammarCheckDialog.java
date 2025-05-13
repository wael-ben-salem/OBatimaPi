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
import javafx.stage.Modality;

import java.util.List;
import java.util.Map;

/**
 * A simple dialog to display grammar check results
 */
public class SimpleGrammarCheckDialog extends Dialog<String> {
    
    private final String originalText;
    private final String correctedText;
    private final TextArea resultTextArea;
    
    /**
     * Create a new dialog to display grammar check results
     * 
     * @param results The results from the grammar checker
     */
    @SuppressWarnings("unchecked")
    public SimpleGrammarCheckDialog(Map<String, Object> results) {
        setTitle("Vérification grammaticale");
        setHeaderText("Résultats de l'analyse grammaticale");
        initModality(Modality.APPLICATION_MODAL);
        
        // Get the original and corrected text
        originalText = (String) results.get("original");
        correctedText = (String) results.get("corrected");
        boolean hasErrors = (boolean) results.get("hasErrors");
        int errorCount = (int) results.get("errorCount");
        
        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        
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
        
        // Initialize the result text area with corrected text
        resultTextArea = new TextArea(correctedText);
        resultTextArea.setWrapText(true);
        resultTextArea.setPrefHeight(100);
        resultTextArea.setPrefWidth(500);
        
        // Add corrections section if there are errors
        if (hasErrors) {
            Text correctionsLabel = new Text("Corrections suggérées:");
            correctionsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            grid.add(correctionsLabel, 0, 2);
            
            // Get the list of corrections
            List<Map<String, Object>> corrections = (List<Map<String, Object>>) results.get("corrections");
            
            // Create a VBox to hold all corrections
            VBox correctionsBox = new VBox(5);
            correctionsBox.setPadding(new Insets(5));
            
            // Add each correction to the VBox
            for (Map<String, Object> correction : corrections) {
                String type = (String) correction.get("type");
                String original = (String) correction.get("original");
                String replacement = (String) correction.get("replacement");
                String description = (String) correction.get("description");
                
                // Create a HBox for this correction
                HBox correctionBox = new HBox(10);
                correctionBox.setAlignment(Pos.CENTER_LEFT);
                correctionBox.setPadding(new Insets(5));
                correctionBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f8f8f8; -fx-background-radius: 5;");
                
                // Add description
                Label descLabel = new Label(description + ":");
                descLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                
                // Add original text
                Label originalLabel2 = new Label("\"" + original + "\"");
                originalLabel2.setTextFill(Color.RED);
                
                // Add arrow
                Label arrowLabel = new Label(" → ");
                
                // Add replacement text
                Label replacementLabel = new Label("\"" + replacement + "\"");
                replacementLabel.setTextFill(Color.GREEN);
                
                // Add apply button
                Button applyButton = new Button("Appliquer");
                applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                
                // Add click handler to apply this specific correction
                applyButton.setOnAction(event -> {
                    String currentText = resultTextArea.getText();
                    String newText = currentText.replace(original, replacement);
                    resultTextArea.setText(newText);
                    applyButton.setDisable(true);
                });
                
                // Add all components to the correction box
                correctionBox.getChildren().addAll(descLabel, originalLabel2, arrowLabel, replacementLabel, applyButton);
                
                // Add the correction box to the corrections VBox
                correctionsBox.getChildren().add(correctionBox);
            }
            
            // Add a ScrollPane to make the corrections scrollable
            ScrollPane scrollPane = new ScrollPane(correctionsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(150);
            grid.add(scrollPane, 0, 3);
            
            // Add a summary
            Text summaryText = new Text(errorCount + " erreur(s) trouvée(s)");
            summaryText.setFill(Color.RED);
            summaryText.setFont(Font.font("System", FontWeight.BOLD, 12));
            grid.add(summaryText, 0, 4);
            
            // Add "Apply All" button
            Button applyAllButton = new Button("Appliquer toutes les corrections");
            applyAllButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            applyAllButton.setOnAction(event -> {
                resultTextArea.setText(correctedText);
                applyAllButton.setDisable(true);
                
                // Disable all individual apply buttons
                correctionsBox.getChildren().forEach(node -> {
                    if (node instanceof HBox) {
                        HBox box = (HBox) node;
                        box.getChildren().forEach(child -> {
                            if (child instanceof Button) {
                                child.setDisable(true);
                            }
                        });
                    }
                });
            });
            grid.add(applyAllButton, 0, 5);
            
        } else {
            // No errors found
            Text noErrorsText = new Text("Aucune erreur trouvée. Le texte est grammaticalement correct.");
            noErrorsText.setFill(Color.GREEN);
            noErrorsText.setFont(Font.font("System", FontWeight.BOLD, 14));
            grid.add(noErrorsText, 0, 2);
        }
        
        // Add result text area section
        Text resultLabel = new Text("Texte final (éditable):");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(resultLabel, 0, 6);
        
        grid.add(resultTextArea, 0, 7);
        
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
