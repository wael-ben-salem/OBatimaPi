
package io.ourbatima.controllers.Reclamation;


import javafx.fxml.FXML;



import io.ourbatima.core.Dao.Reclamation.ReclamationDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Reclamation;
import io.ourbatima.core.model.Utilisateur.Utilisateur;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateReclamationController extends ActionView {

    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<Integer> utilisateurIdComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final ReclamationDAO reclamationDAO = new ReclamationDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Reclamation reclamationToUpdate;

    @FXML
    public void initialize() {
        // Load status options when the window opens
        System.out.println("UpdateReclamationController Initialized");
        updateButton.setOnAction(event -> updateReclamation());
        cancelButton.setOnAction(event -> closePopup());

        // Set default date to current date
        datePicker.setValue(java.time.LocalDate.now());

        // Set default status to "NEW"
        statutComboBox.setValue("NEW");

        // Load users into the ComboBox
        loadUtilisateurs();
    }

    private void loadUtilisateurs() {
        // Fetch all utilisateurs from the database and populate the ComboBox
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllUsers();
        for (Utilisateur utilisateur : utilisateurs) {
            utilisateurIdComboBox.getItems().add(utilisateur.getId());
        }
    }

    /*private void loadStatuts() {
        List<String> statuts = Arrays.asList("Ouverte", "En cours", "Résolue", "Fermée");
        statutComboBox.setItems(FXCollections.observableArrayList(statuts));
    }*/

    public void setReclamationToUpdate(Reclamation reclamation) {
        this.reclamationToUpdate = reclamation;


        if (reclamation != null) {
            descriptionField.setText(reclamation.getDescription());
            statutComboBox.setValue(reclamation.getStatut());
            datePicker.setValue(reclamation.getDate().toLocalDate());
            utilisateurIdComboBox.setValue(reclamation.getUtilisateurId());
        }
    }

    @FXML
    public void updateReclamation() {
        if (descriptionField.getText().isEmpty() ||
                statutComboBox.getValue() == null || datePicker.getValue() == null || utilisateurIdComboBox.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        if (reclamationToUpdate == null) {
            System.out.println("❌ ERROR: Aucune réclamation à mettre à jour!");
            return;
        }

        try {
            // Create updated reclamation with the same ID as the original
            Reclamation updatedReclamation = new Reclamation(
                    reclamationToUpdate.getId(),
                    descriptionField.getText(),
                    statutComboBox.getValue(),
                    datePicker.getValue().atStartOfDay(),
                    utilisateurIdComboBox.getValue()
            );

            reclamationDAO.updateReclamation(updatedReclamation);
            System.out.println("✅ Réclamation mise à jour avec succès ! ID: " + reclamationToUpdate.getId());
            closePopup();
        } catch (Exception e) {
            System.out.println("❌ ERROR: Erreur lors de la mise à jour !");
            e.printStackTrace();
        }
    }

    @FXML
    public void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setReclamationId(int idReclamation) {
        Reclamation reclamation = reclamationDAO.getReclamationById(idReclamation);
        setReclamationToUpdate(reclamation);
    }

    //public void setId(int id){}
}