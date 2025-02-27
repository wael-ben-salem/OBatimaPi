package io.ourbatima.controllers.projet;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class UpdateProjet {

    @FXML
    private TextField nomProjetField;
    @FXML private TextField budgetField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField styleArchField;
    @FXML private ComboBox<String> etatComboBox;
    private Projet projet;
    private AfficherProjet afficherProjetController;

    public void initData(Projet projet, AfficherProjet controller) {
        this.projet = projet;
        this.afficherProjetController = controller;

        nomProjetField.setText(projet.getNomProjet());
        budgetField.setText(projet.getBudget().toString());
        typeComboBox.getItems().addAll("Résidentiel", "Commercial", "Industriel", "Infrastructure", "Institutionnel", "Mixte", "Civique");
        typeComboBox.setValue(projet.getType());
        styleArchField.setText(projet.getStyleArch());
        etatComboBox.getItems().addAll("En attente", "En cours", "Terminé");
    }

    @FXML
    private void handleUpdate() {
        try {
            BigDecimal budgetValue = new BigDecimal(budgetField.getText());

            projet.setNomProjet(nomProjetField.getText());
            projet.setBudget(budgetValue);
            String selectedType = typeComboBox.getValue();
            if (selectedType != null && !selectedType.isEmpty()) {
                projet.setType(selectedType);
            }            projet.setStyleArch(styleArchField.getText());
            String selectedEtat = etatComboBox.getValue();
            if (selectedEtat != null && !selectedEtat.isEmpty()) {
                projet.setEtat(selectedEtat);
            } else {
                // Retain the existing value
                projet.setEtat(projet.getEtat());
            }
            ProjetDAO projetDAO = new ProjetDAO();
            projetDAO.updateProjet(projet);

            afficherProjetController.loadProjets();
            ((Stage) nomProjetField.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid budget format!");
            alert.showAndWait();
        }
    }
}