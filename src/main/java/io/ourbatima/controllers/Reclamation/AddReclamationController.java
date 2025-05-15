package io.ourbatima.controllers.Reclamation;

import io.ourbatima.core.Dao.Reclamation.ReclamationDAO;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Reclamation;

import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


/*public class AddReclamationController extends ActionView {

    @FXML
    private TextArea descriptionAreaField; // TextArea pour la description
    @FXML
    private TextField statutTextField; // TextField pour le statut
    @FXML
    private DatePicker dateField;
    @FXML
    private Button ajouterButton;

    private ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML
    private void initialize() {
        System.out.println("AjoutReclamationController Initialized");
        ajouterButton.setOnAction(event -> handleAddReclamation());
        addInputListeners();
    }

    private void addInputListeners() {
        descriptionAreaField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        statutTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
    }

    private void validateInput() {
        boolean isValid = !descriptionAreaField.getText().trim().isEmpty()
                && !statutTextField.getText().trim().isEmpty()
                && dateField.getValue() != null;

        ajouterButton.setDisable(!isValid);
    }

    @FXML
    private void handleAddReclamation() {
        String description = descriptionAreaField.getText().trim();
        String statut = statutTextField.getText().trim();
        LocalDate date = dateField.getValue();

        if (description.isEmpty()) {
            showError("La description ne peut pas être vide.");
            return;
        }

        if (statut.isEmpty()) {
            showError("Veuillez entrer un statut.");
            return;
        }

        if (date == null) {
            showError("Veuillez sélectionner une date.");
            return;
        }

        Reclamation reclamation = new Reclamation(description, statut, date.atStartOfDay());

        int reclamationId = reclamationDAO.addReclamation(reclamation);

        if (reclamationId != -1) {
            showSuccess("Réclamation ajoutée avec succès !");
            resetFields();
        } else {
            showError("Erreur lors de l'ajout de la réclamation.");
        }
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

    private void resetFields() {
        descriptionAreaField.clear();
        statutTextField.clear();
        dateField.setValue(null);
    }
}*/
public class AddReclamationController extends ActionView {

    @FXML
    private TextArea descriptionAreaField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<Integer> utilisateurIdComboBox; // Changed to ComboBox
    @FXML
    private DatePicker dateField;
    @FXML
    private Button ajouterButton;

    private ReclamationDAO reclamationDAO = new ReclamationDAO();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    private void initialize() {
        System.out.println("AddReclamation Controller Initialized");
        ajouterButton.setOnAction(event -> handleAddReclamation());
        loadUtilisateurs(); // Load utilisateurs into the ComboBox
        addInputListeners();

        // Set default date to current date
        dateField.setValue(LocalDate.now());

        // Set default status to "NEW"
        statutComboBox.setValue("NEW");
    }

    private void loadUtilisateurs() {
        // Fetch all utilisateurs from the database and populate the ComboBox
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllUsers();
        for (Utilisateur utilisateur : utilisateurs) {
            utilisateurIdComboBox.getItems().add(utilisateur.getId());
        }
    }

    private void addInputListeners() {
        descriptionAreaField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        statutComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
        utilisateurIdComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateInput()); // Listen for ComboBox changes
        dateField.valueProperty().addListener((observable, oldValue, newValue) -> validateInput());
    }

    private void validateInput() {
        boolean isValid = !descriptionAreaField.getText().trim().isEmpty() &&
                statutComboBox.getValue() != null &&
                utilisateurIdComboBox.getValue() != null && // Ensure a user is selected
                dateField.getValue() != null; // Ensure a date is selected

        ajouterButton.setDisable(!isValid);
    }

    @FXML
    private void handleAddReclamation() {
        String description = descriptionAreaField.getText().trim();
        String statut = statutComboBox.getValue();
        LocalDateTime date = dateField.getValue().atStartOfDay();
        Integer utilisateurId = utilisateurIdComboBox.getValue(); // Get selected user ID from ComboBox

        if (description.isEmpty()) {
            showError("Description de la réclamation est requise");
            return;
        }

        if (statut == null) {
            showError("Statut de la réclamation est requis");
            return;
        }

        if (dateField.getValue() == null) {
            showError("Date de la réclamation est requise");
            return;
        }

        if (utilisateurId == null) {
            showError("Utilisateur non sélectionné");
            return;
        }

        // Convert LocalDate to LocalDateTime for the timestamp


        Reclamation reclamation = new Reclamation(0, description, statut, date, utilisateurIdComboBox.getValue());

        int reclamationId = reclamationDAO.addReclamation(reclamation);

        if (reclamationId != -1) {
            showSuccess("Réclamation ajoutée avec succès");
            resetFields();
        } else {
            showError("Erreur lors de l'ajout de la réclamation");
        }
    }

    private void resetFields() {
        descriptionAreaField.clear();
        statutComboBox.setValue("NEW"); // Reset to default status
        utilisateurIdComboBox.setValue(null); // Reset the ComboBox
        dateField.setValue(LocalDate.now()); // Reset to current date
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