package io.OurBatima.controllers;

import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReclamationController extends ActionView {

    @FXML
    private TextField idField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField statutField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private ListView<Reclamation> reclamationListView;

    private ReclamationDAO reclamationDAO;

    public void initialize() {
        reclamationDAO = new ReclamationDAO();
        loadReclamations();
    }

    @FXML
    private void handleAddReclamation() {
        Reclamation reclamation = new Reclamation();
        reclamation.setDescription(descriptionField.getText());
        reclamation.setStatut(statutField.getText());
        reclamation.setDate(LocalDateTime.now());
        reclamationDAO.add(reclamation);
        loadReclamations();
    }

    @FXML
    private void handleUpdateReclamation() {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            selectedReclamation.setDescription(descriptionField.getText());
            selectedReclamation.setStatut(statutField.getText());
            reclamationDAO.update(selectedReclamation);
            loadReclamations();
        }
    }

    @FXML
    private void handleDeleteReclamation() {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            reclamationDAO.delete(selectedReclamation.getId());
            loadReclamations();
        }
    }

    private void loadReclamations() {
        reclamationListView.getItems().clear();
        reclamationListView.getItems().addAll(reclamationDAO.getAll());
    }
}
