package io.OurBatima.controllers.Reponse;


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.List;

public class AddReponseController extends ActionView {

    @FXML
    private TextArea descriptionAreaField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<Reclamation> reclamationIdComboBox;
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
        loadReclamations();
        addInputListeners();

        // Set default date to current date
        dateField.setValue(java.time.LocalDate.now());

        // Set default status
        statutComboBox.setValue("Pending");
    }

    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
        reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamations));

        // Set up a custom cell factory to display reclamation details
        reclamationIdComboBox.setConverter(new StringConverter<Reclamation>() {
            @Override
            public String toString(Reclamation reclamation) {
                if (reclamation == null) {
                    return "";
                }
                return "ID: " + reclamation.getId() + " - " +
                       truncateText(reclamation.getDescription(), 30) + " - " +
                       reclamation.getStatut();
            }

            @Override
            public Reclamation fromString(String string) {
                return null; // Not needed for this use case
            }
        });
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
            String description = descriptionAreaField.getText().trim();
            String statut = statutComboBox.getValue();
            LocalDateTime date = dateField.getValue().atStartOfDay();
            // Make sure we have a valid reclamation selected
            if (reclamationIdComboBox.getValue() == null) {
                showError("Veuillez sélectionner une réclamation");
                return;
            }
            Integer reclamationId = reclamationIdComboBox.getValue().getId();

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
