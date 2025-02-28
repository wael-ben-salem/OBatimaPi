package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class EquipeUpdateController extends ActionView {

    @FXML private TextField nomField;
    @FXML private ComboBox<String> constructeurComboBox;
    @FXML private ComboBox<String> gestionnaireComboBox;
    @FXML private DatePicker dateCreationPicker;
    @FXML private ComboBox<String> artisansComboBox;

    private Equipe equipe;
    private EquipeListController equipeListController;

    public void initData(Equipe equipe) {
        this.equipe = equipe;
        loadData();
    }

    public void setEquipeListController(EquipeListController controller) {
        this.equipeListController = controller;
    }

    private void loadData() {
        if (equipe != null) {
            nomField.setText(equipe.getNom());
            dateCreationPicker.setValue(equipe.getDateCreation());

            // Charger les constructeurs et gestionnaires (à adapter selon votre DAO)
            // constructeurComboBox.getItems().addAll(...);
            // gestionnaireComboBox.getItems().addAll(...);

            // Sélectionner les valeurs actuelles
            constructeurComboBox.getSelectionModel().select(equipe.getConstructeur().getNom() + " " + equipe.getConstructeur().getPrenom());
            gestionnaireComboBox.getSelectionModel().select(equipe.getGestionnaireStock().getNom() + " " + equipe.getGestionnaireStock().getPrenom());

            // Charger les artisans disponibles
            // artisansComboBox.getItems().addAll(...);
        }
    }

    @FXML
    private void updateEquipe() {
        try {
            // Mettre à jour les propriétés de l'équipe
            equipe.setNom(nomField.getText());
            equipe.setDateCreation(dateCreationPicker.getValue());

            // Mettre à jour le constructeur et le gestionnaire (à adapter selon votre DAO)
            // equipe.setConstructeur(...);
            // equipe.setGestionnaireStock(...);

            // Mettre à jour les artisans (à adapter selon votre DAO)
            // equipe.setArtisans(...);

            // Sauvegarder les modifications
            EquipeDAO equipeDAO = new EquipeDAO();
            equipeDAO.update(equipe);

            // Rafraîchir la liste des équipes
            if (equipeListController != null) {
                equipeListController.refreshEquipeList();
            }

            // Fermer la fenêtre
            close();

        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher une erreur à l'utilisateur
        }
    }

    @FXML
    private void close() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}