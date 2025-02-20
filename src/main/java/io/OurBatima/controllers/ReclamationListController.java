package io.OurBatima.controllers;

import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.sql.Timestamp;
import java.util.List;

public class ReclamationListController extends ActionView {

    @FXML
    private GridPane reclamationGrid;
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML
    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationDAO.getAll();

        // Clear existing data rows (keep header row 0)
        reclamationGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Add new data rows
        int rowIndex = 1;
        for (Reclamation reclamation : reclamations) {
            // ID (non-editable)
            TextField idField = createTextField(String.valueOf(reclamation.getId()));
            idField.setEditable(false); // ID should not be editable

            // Description (editable)
            TextField descriptionField = createTextField(reclamation.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reclamation.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reclamation.getDate().toString());
            dateField.setEditable(false); // Date should not be editable

            // Save Button
            Button saveButton = new Button("Save");
            saveButton.setOnAction(event -> {
                // Update the reclamation object
                reclamation.setDescription(descriptionField.getText());
                reclamation.setStatut(statutField.getText());

                // Save to database
                reclamationDAO.update(reclamation);
                System.out.println("Reclamation updated successfully: " + reclamation.getId());
            });

            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                // Delete the reclamation from the database
                reclamationDAO.delete(reclamation.getId());
                System.out.println("Reclamation deleted successfully: " + reclamation.getId());
                // Reload the reclamations after deletion
                loadReclamations();
            });

            // Add components to the GridPane
            reclamationGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Reclamations loaded: " + reclamations.size());
    }

    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
    }
}