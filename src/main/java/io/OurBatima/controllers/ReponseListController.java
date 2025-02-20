package io.OurBatima.controllers;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.sql.Timestamp;
import java.util.List;

public class ReponseListController extends ActionView {

    @FXML private GridPane reponseGrid;
    private final ReponseDAO reponseDAO = new ReponseDAO();

    @FXML
    private void loadReponses() {
        List<Reponse> reponses = reponseDAO.getAll();

        // Clear existing data rows (keep header row 0)
        reponseGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Add new data rows
        int rowIndex = 1;
        for (Reponse reponse : reponses) {
            // ID (non-editable)
            TextField idField = createTextField(String.valueOf(reponse.getId()));
            idField.setEditable(false); // ID should not be editable

            // Reclamation ID (non-editable)
            TextField reclamationIdField = createTextField(String.valueOf(reponse.getReclamationId()));
            reclamationIdField.setEditable(false); // Reclamation ID should not be editable

            // Description (editable)
            TextField descriptionField = createTextField(reponse.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reponse.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reponse.getDate().toString());
            dateField.setEditable(false); // Date should not be editable

            // Save Button
            Button saveButton = new Button("Save");
            saveButton.setOnAction(event -> {
                // Update the reponse object
                reponse.setDescription(descriptionField.getText());
                reponse.setStatut(statutField.getText());

                // Save to database
                reponseDAO.update(reponse);
                System.out.println("Reponse updated successfully: " + reponse.getId());
            });

            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                // Delete the reponse from the database
                reponseDAO.delete(reponse.getId());
                System.out.println("Reponse deleted successfully: " + reponse.getId());
                // Reload the reponses after deletion
                loadReponses();
            });

            // Add components to the GridPane
            reponseGrid.addRow(rowIndex, idField, reclamationIdField, descriptionField, statutField, dateField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Reponses loaded: " + reponses.size());
    }

    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
    }
}