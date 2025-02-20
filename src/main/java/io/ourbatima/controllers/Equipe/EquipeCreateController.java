package io.ourbatima.controllers.Equipe;


import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EquipeCreateController extends ActionView {

    @FXML private TextField nomField;
    @FXML private ListView<?> membersListView;

    private EquipeListController equipeListController;

    public void setEquipeListController(EquipeListController controller) {
        this.equipeListController = controller;
    }

    @FXML
    private void createEquipe() throws SQLException {
        Equipe nouvelleEquipe = new Equipe();
        nouvelleEquipe.setNom(nomField.getText());
        // Ajouter la logique pour les membres sélectionnés

        EquipeDAO equipeDAO = new EquipeDAO();
        equipeDAO.create(nouvelleEquipe);

        if (equipeListController != null) {
            equipeListController.refreshEquipeList();
        }
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}