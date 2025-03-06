package io.OurBatima.controllers.Reponse;

import io.OurBatima.controllers.Reclamation.UpdateReclamationController;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reponse;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
public class ListReponseController extends ActionView {


    @FXML private GridPane reponseGrid;
    private final ReponseDAO reponseDAO = new ReponseDAO();

    @FXML
    private void loadReponses() {
        List<Reponse> reponses = reponseDAO.getAllReponses();

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
            idField.setEditable(false);

            // Description (editable)
            TextField descriptionField = createTextField(reponse.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reponse.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reponse.getDate().toString());
            dateField.setEditable(false);

            TextField ReponseIdField = createTextField(String.valueOf(reponse.getIdReclamation()));
            ReponseIdField.setEditable(false);


            // Save Button
            Button saveButton = new Button("Update");
            saveButton.setOnAction(e -> openUpdatePopup(reponse.getId()));

            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                reponseDAO.deleteReponse(reponse.getId());
                System.out.println("Reponse deleted successfully: " + reponse.getId());
                loadReponses();
            });

            // Add components to the GridPane
            reponseGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField,ReponseIdField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Reponses loaded: " + reponses.size());
    }



    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
    }
    private void openUpdatePopup(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reponse/UpdateReponse.fxml"));
            Parent root = loader.load();

            UpdateReponseController UpdateReponseController = loader.getController();
             //updateReclamationController.setId(int id);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Reclamation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField searchField; // Champ de texte pour entrer l'ID ou le critère de recherche

    @FXML
    private Label resultLabel;

    @FXML
    private void searchReponse() {
        String searchInput = searchField.getText().trim();

        if (searchInput.isEmpty()) {
            resultLabel.setText("Veuillez entrer un critère de recherche.");
            return;
        }

        try {
            // Recherche par ID (supposons que l'entrée est un ID)
            int id = Integer.parseInt(searchInput);
            Reponse reponse = reponseDAO.getReponseById(id);

            if (reponse != null) {
                // Afficher les détails dans le Label
                String result = String.format("ID: %d\nDescription: %s\nStatut: %s\nDate: %s\nID Réclamation: %d",
                        reponse.getId(),
                        reponse.getDescription(),
                        reponse.getStatut(),
                        reponse.getDate().toString(),
                        reponse.getIdReclamation());
                resultLabel.setText(result);
            } else {
                resultLabel.setText("Aucune réponse trouvée pour l'ID " + id);
            }
        } catch (NumberFormatException e) {
            // Si l'entrée n'est pas un nombre, on pourrait rechercher par description (optionnel)
            List<Reponse> allReponses = reponseDAO.getAllReponses();
            Reponse foundReponse = allReponses.stream()
                    .filter(r -> r.getDescription().toLowerCase().contains(searchInput.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (foundReponse != null) {
                String result = String.format("ID: %d\nDescription: %s\nStatut: %s\nDate: %s\nID Réclamation: %d",
                        foundReponse.getId(),
                        foundReponse.getDescription(),
                        foundReponse.getStatut(),
                        foundReponse.getDate().toString(),
                        foundReponse.getIdReclamation());
                resultLabel.setText(result);
            } else {
                resultLabel.setText("Aucune réponse trouvée pour '" + searchInput + "'");
            }
        }
    }
}





