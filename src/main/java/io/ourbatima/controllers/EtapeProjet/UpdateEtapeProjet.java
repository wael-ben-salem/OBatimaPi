package io.ourbatima.controllers.EtapeProjet;

import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;

public class UpdateEtapeProjet {

    @FXML
    private TextField nomEtapeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private TextField montantField;

    private EtapeProjetDAO etapeProjetDAO;
    private EtapeProjet etapeProjet;

    @FXML
    public void initializeWithEtapeProjet(EtapeProjet etapeProjet) {
        this.etapeProjet = etapeProjet;
        this.etapeProjetDAO = new EtapeProjetDAO();

        nomEtapeField.setText(etapeProjet.getNomEtape());
        descriptionField.setText(etapeProjet.getDescription());
        dateDebutPicker.setValue(etapeProjet.getDateDebut().toLocalDate());
        dateFinPicker.setValue(etapeProjet.getDateFin().toLocalDate());
        statutComboBox.getItems().addAll("En attente", "En cours", "Terminé");
        statutComboBox.setValue(etapeProjet.getStatut());
        montantField.setText(etapeProjet.getMontant().toString());
    }


    @FXML
    private void handleUpdate() {

        etapeProjet.setNomEtape(nomEtapeField.getText());
        etapeProjet.setDescription(descriptionField.getText());
        etapeProjet.setDateDebut(Date.valueOf(dateDebutPicker.getValue()));
        etapeProjet.setDateFin(Date.valueOf(dateFinPicker.getValue()));
        etapeProjet.setStatut(statutComboBox.getValue());
        etapeProjet.setMontant(new BigDecimal(montantField.getText()));

        etapeProjetDAO.updateEtapeProjet(etapeProjet);
        // Optionally, close the window after updating
        ((Stage) nomEtapeField.getScene().getWindow()).close();
    }

}