package io.OurBatima.core.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.util.List;
import java.util.Map;

/**
 * Dialog for displaying grammar check results
 */
public class GrammarCheckResultDialog extends Dialog<ButtonType> {
    
    /**
     * Create a new dialog to display grammar check results
     * 
     * @param results The results from the grammar checker
     */
    public GrammarCheckResultDialog(Map<String, Object> results) {
        setTitle("Résultats de la vérification grammaticale");
        setHeaderText("Analyse grammaticale");
        initModality(Modality.APPLICATION_MODAL);
        
        // Create the content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        boolean success = (boolean) results.get("success");
        
        if (success) {
            // Get the original and corrected text
            String originalText = (String) results.get("original");
            String correctedText = (String) results.get("corrected");
            int errors = ((Number) results.get("errors")).intValue();
            
            // Add original text section
            Text originalLabel = new Text("Texte original:");
            originalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            grid.add(originalLabel, 0, 0);
            
            TextArea originalTextArea = new TextArea(originalText);
            originalTextArea.setEditable(false);
            originalTextArea.setWrapText(true);
            originalTextArea.setPrefHeight(100);
            originalTextArea.setPrefWidth(400);
            grid.add(originalTextArea, 0, 1);
            
            // Add corrected text section
            Text correctedLabel = new Text("Texte corrigé:");
            correctedLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            grid.add(correctedLabel, 0, 2);
            
            TextArea correctedTextArea = new TextArea(correctedText);
            correctedTextArea.setEditable(false);
            correctedTextArea.setWrapText(true);
            correctedTextArea.setPrefHeight(100);
            correctedTextArea.setPrefWidth(400);
            grid.add(correctedTextArea, 0, 3);
            
            // Add summary section
            VBox summaryBox = new VBox(5);
            
            if (errors > 0) {
                Text errorsText = new Text(errors + " erreur(s) trouvée(s)");
                errorsText.setFill(Color.RED);
                errorsText.setFont(Font.font("System", FontWeight.BOLD, 14));
                summaryBox.getChildren().add(errorsText);
                
                if (results.containsKey("similarity")) {
                    Text similarityText = new Text("Similarité: " + results.get("similarity"));
                    summaryBox.getChildren().add(similarityText);
                }
                
                // Add details if available
                if (results.containsKey("details")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> details = (List<Map<String, Object>>) results.get("details");
                    
                    if (!details.isEmpty()) {
                        Text detailsLabel = new Text("Détails des corrections:");
                        detailsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                        summaryBox.getChildren().add(detailsLabel);
                        
                        for (Map<String, Object> detail : details) {
                            String description = (String) detail.get("description");
                            String bad = (String) detail.get("bad");
                            @SuppressWarnings("unchecked")
                            String[] better = (String[]) detail.get("better");
                            
                            VBox detailBox = new VBox(2);
                            detailBox.setPadding(new Insets(5));
                            detailBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f8f8f8; -fx-background-radius: 5;");
                            
                            Text descText = new Text(description);
                            descText.setFont(Font.font("System", FontWeight.BOLD, 12));
                            
                            Text badText = new Text("Incorrect: " + bad);
                            badText.setFill(Color.RED);
                            
                            Text betterText = new Text("Suggestion: " + better[0]);
                            betterText.setFill(Color.GREEN);
                            
                            detailBox.getChildren().addAll(descText, badText, betterText);
                            summaryBox.getChildren().add(detailBox);
                        }
                    }
                }
            } else {
                Text noErrorsText = new Text("Aucune erreur trouvée. Le texte est grammaticalement correct.");
                noErrorsText.setFill(Color.GREEN);
                noErrorsText.setFont(Font.font("System", FontWeight.BOLD, 14));
                summaryBox.getChildren().add(noErrorsText);
            }
            
            grid.add(summaryBox, 0, 4);
            
        } else {
            // Display error message
            String message = (String) results.get("message");
            Label errorLabel = new Label("Erreur: " + message);
            errorLabel.setTextFill(Color.RED);
            grid.add(errorLabel, 0, 0);
        }
        
        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }
}
