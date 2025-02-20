package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class EquipeListController extends ActionView implements Initializable {

    @FXML private FlowPane equipeFlowPane;
    @FXML private Label titleLabel;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageIndicator;

    private EquipeDAO equipeDAO = new EquipeDAO();
    private int currentPage = 0;
    private int totalEquipes = 0;
    private final int pageSize = 9; // Nombre d'équipes par page

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshEquipeList();
    }

    void refreshEquipeList() {
        equipeFlowPane.getChildren().clear();

        try {
            List<Equipe> allEquipes = equipeDAO.findAll();
            totalEquipes = allEquipes.size();

            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, totalEquipes);
            List<Equipe> pageEquipes = allEquipes.subList(start, end);

            for (Equipe equipe : pageEquipes) {
                HBox card = createEquipeCard(equipe);
                equipeFlowPane.getChildren().add(card);
            }

            updatePaginationControls();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createEquipeCard(Equipe equipe) {
        HBox card = new HBox(10);
        card.getStyleClass().add("equipe-card");
        card.setPadding(new Insets(15));

        // Logo de l'équipe (icône par défaut)
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/team.png")));
        logo.setFitWidth(80);
        logo.setFitHeight(80);
        logo.setPreserveRatio(true);

        // Informations de l'équipe
        VBox info = new VBox(5);
        Label nameLabel = new Label(equipe.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label membersLabel = new Label("Membres : " + equipe.getArtisans().size());
        membersLabel.setStyle("-fx-font-size: 14px;");

        info.getChildren().addAll(nameLabel, membersLabel);
        card.getChildren().addAll(logo, info);

        return card;
    }

    private void updatePaginationControls() {
        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable((currentPage + 1) * pageSize >= totalEquipes);
        pageIndicator.setText("Page " + (currentPage + 1));
    }

    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            refreshEquipeList();
        }
    }

    @FXML
    private void nextPage() {
        if ((currentPage + 1) * pageSize < totalEquipes) {
            currentPage++;
            refreshEquipeList();
        }
    }

    @FXML
    private void openAddEquipeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipeadd.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshEquipeList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}