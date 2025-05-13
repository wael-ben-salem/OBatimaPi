package io.OurBatima.controllers.Reponse;


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddReponseController extends ActionView {

    @FXML
    private TextArea descriptionAreaField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<Integer> reclamationIdComboBox;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button ajouterButton;

    private ReponseDAO reponseDAO = new ReponseDAO();
    private ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML
    private void initialize() {
        System.out.println("AddReponse Controller Initialized");
        ajouterButton.setOnAction(event -> handleAddReponse());

        // Load reclamations from database
        loadReclamations();

        // If the ComboBox is still empty, add some manual items
        if (reclamationIdComboBox.getItems().isEmpty()) {
            addManualReclamations();
        }

        addInputListeners();

        // Set default date to current date
        dateField.setValue(java.time.LocalDate.now());

        // Set default status
        statutComboBox.setValue("Pending");
    }

    /**
     * Adds manual reclamation IDs to the ComboBox if database loading fails
     */
    private void addManualReclamations() {
        System.out.println("Adding manual reclamation IDs...");

        // Create a list of simple integer IDs (1-5)
        List<Integer> reclamationIds = new ArrayList<>();
        reclamationIds.add(1);
        reclamationIds.add(2);
        reclamationIds.add(3);
        reclamationIds.add(4);
        reclamationIds.add(5);

        // Add them to the ComboBox
        reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamationIds));

        System.out.println("Added " + reclamationIds.size() + " manual reclamation IDs");
    }

    private void loadReclamations() {
        try {
            System.out.println("Starting to load reclamation IDs...");

            // Get all reclamations from the database
            List<Reclamation> reclamations = reclamationDAO.getAllReclamations();

            // Check if we have any reclamations
            if (reclamations == null || reclamations.isEmpty()) {
                System.out.println("No reclamations found in database, using manual IDs");
                addManualReclamations();
                return;
            }

            // Clear existing items
            reclamationIdComboBox.getItems().clear();

            // Extract just the IDs from the reclamations
            List<Integer> reclamationIds = new ArrayList<>();
            for (Reclamation r : reclamations) {
                reclamationIds.add(r.getId());
                System.out.println("Added ID: " + r.getId());
            }

            // Add IDs to the ComboBox
            reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamationIds));

            // Debug output
            System.out.println("Loaded " + reclamationIds.size() + " reclamation IDs");
        } catch (Exception e) {
            System.out.println("ERROR loading reclamations: " + e.getMessage());
            e.printStackTrace();
            addManualReclamations();
        }
    }

    // Helper method to truncate long descriptions
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private void addInputListeners() {
        descriptionAreaField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        statutComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
        reclamationIdComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
    }

    private void validateInput() {
        boolean isValid = !descriptionAreaField.getText().trim().isEmpty() &&
                statutComboBox.getValue() != null &&
                dateField.getValue() != null &&
                reclamationIdComboBox.getValue() != null;
        ajouterButton.setDisable(!isValid);
    }


    @FXML
    private void handleAddReponse() {
        try {
            System.out.println("handleAddReponse called");

            String description = descriptionAreaField.getText().trim();
            String statut = statutComboBox.getValue();
            LocalDateTime date = dateField.getValue().atStartOfDay();

            // Debug output
            System.out.println("Description: " + description);
            System.out.println("Statut: " + statut);
            System.out.println("Date: " + date);
            System.out.println("ReclamationIdComboBox value: " + reclamationIdComboBox.getValue());

            // Make sure we have a valid reclamation selected
            if (reclamationIdComboBox.getValue() == null) {
                showError("Veuillez sélectionner une réclamation");
                return;
            }

            // Get the reclamation ID directly from the ComboBox
            Integer reclamationId = reclamationIdComboBox.getValue();
            System.out.println("Reclamation ID: " + reclamationId);

            if (description.isEmpty()) {
                showError("La description est requise");
                return;
            }
            if (statut.isEmpty()) {
                showError("Le statut est requis");
                return;
            }
            if (date == null) {
                showError("La date est requise");
                return;
            }
            if (reclamationId == null) {
                showError("Une réclamation doit être sélectionnée");
                return;
            }


            Reponse reponse = new Reponse(1, description, statut, date,reclamationId);
            int reponseId = reponseDAO.addReponse(reponse);

            if (reponseId != -1) {
                showSuccess("Réponse ajoutée avec succès");
                resetFields();
            } else {
                showError("Erreur lors de l'ajout de la réponse");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    private void resetFields() {
        descriptionAreaField.clear();
        statutComboBox.setValue("Pending"); // Set default status
        reclamationIdComboBox.setValue(null);
        dateField.setValue(java.time.LocalDate.now()); // Reset to current date
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
