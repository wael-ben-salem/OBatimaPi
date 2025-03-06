package io.OurBatima.controllers.Reponse;


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AddReponseController extends ActionView {

    @FXML
    private TextArea descriptionAreaField;
    @FXML
    private TextField statutTextField;
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
       loadReclamations();
        addInputListeners();
    }

    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
        for (Reclamation reclamation : reclamations) {
            reclamationIdComboBox.getItems().add(reclamation.getId());
        }
    }

    private void addInputListeners() {
        descriptionAreaField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        statutTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        reclamationIdComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
    }

    private void validateInput() {
        boolean isValid = !descriptionAreaField.getText().trim().isEmpty() &&
                !statutTextField.getText().trim().isEmpty() &&dateField.getValue() != null ||
                reclamationIdComboBox.getValue() != null
                ;
        ajouterButton.setDisable(!isValid);
    }


    @FXML
    private void handleAddReponse() {
        try {
            String description = descriptionAreaField.getText().trim();
            String statut = statutTextField.getText().trim();
            LocalDateTime date = dateField.getValue().atStartOfDay();
            Integer reclamationId = reclamationIdComboBox.getValue();

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
        statutTextField.clear();
        reclamationIdComboBox.setValue(null);
        dateField.setValue(null);
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
