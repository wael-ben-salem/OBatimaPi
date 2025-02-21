package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EquipeCardController extends ActionView implements Initializable {

    @FXML
    private HBox cardRoot;
    @FXML private ImageView teamLogo;
    @FXML private Label teamNameLabel;
    @FXML private Label membersCountLabel;

    private EquipeListController equipeListController;
    private Equipe equipe;

    public void setEquipeData(Equipe equipe) {
        this.equipe = equipe;
        updateUI();
    }

    public void setEquipeListController(EquipeListController controller) {
        this.equipeListController = controller;
    }

    private void updateUI() {
        teamNameLabel.setText(equipe.getNom());
        membersCountLabel.setText("Membres : " + equipe.getArtisans().size());
        loadLogo(equipe);
    }

    private void loadLogo(Equipe equipe) {
        try {
            String imagePath = equipe.getNom() != null ?
                    equipe.getNom() : "/images/team.png";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            teamLogo.setImage(image);
        } catch (Exception e) {
            System.err.println("Erreur de chargement du logo: " + e.getMessage());
        }
    }

    @FXML
    private void updateEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipeupdate.fxml"));
            Parent root = loader.load();

            EquipeUpdateController controller = loader.getController();
            controller.initData(equipe);
            controller.setEquipeListController(equipeListController);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            updateUI();
            equipeListController.refreshEquipeList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEquipe() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette équipe ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            EquipeDAO equipeDAO = new EquipeDAO();
            equipeDAO.delete(equipe.getId());
            equipeListController.refreshEquipeList();
        }
    }

    @FXML
    private void showDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipedetail.fxml"));
            Parent root = loader.load();

            EquipeDetailController controller = loader.getController();
            controller.initData(equipe);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du style
        cardRoot.getStyleClass().add("equipe-card");
    }
}