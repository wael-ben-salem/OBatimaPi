package io.OurBatima.controllers;


import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class reponseController extends ActionView {

    @FXML
    private TextField reclamationIdField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField statutField;

    @FXML
    private DatePicker dateField;

    private final ReponseDAO reponseDAO = new ReponseDAO();

    public void goToHome(ActionEvent actionEvent) {
        context.routes().setView("drawer");
    }

    public void handleAddReponse(ActionEvent actionEvent) {
        String reclamationIdText = reclamationIdField.getText().trim();
        String description = descriptionField.getText().trim();
        String statut = statutField.getText().trim();

        // Validate inputs
        if (reclamationIdText.isEmpty() || !reclamationIdText.chars().allMatch(Character::isDigit)) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "L'ID de la réclamation doit être un nombre valide.");
            return;
        }

        int reclamationId = Integer.parseInt(reclamationIdText);

        if (description.length() < 5) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "La description doit contenir au moins 5 caractères.");
            return;
        }

        if (statut.length() < 3) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le statut doit contenir au moins 3 caractères.");
            return;
        }

        Reponse reponse = new Reponse();
        reponse.setReclamationId(reclamationId);
        reponse.setDescription(description);
        reponse.setStatut(statut);
        reponse.setDate(LocalDateTime.now());

        boolean isSaved = reponseDAO.add(reponse);

        if (isSaved) {
            showAlert(AlertType.INFORMATION, "Succès", "La réponse a été ajoutée avec succès.");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout de la réponse.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        reclamationIdField.clear();
        descriptionField.clear();
        statutField.clear();
    }
}