package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Constructeur;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Utilisateur.GestionnaireDeStock;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EquipeListController extends ActionView implements Initializable {

    @FXML private FlowPane equipeFlowPane;
    @FXML private Label titleLabel;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageIndicator;

    private EquipeDAO equipeDAO = new EquipeDAO();
    private int currentPage = 0;
    private int totalEquipes = 0;
    private Runnable refreshParent;


    private final int pageSize = 9; // Nombre d'équipes par page

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupAutoRefresh();

        refreshEquipeList();
    }
    private void setupAutoRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> refreshEquipeList())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipecard.fxml"));

            // Récupérer le HBox défini dans le fichier FXML
            HBox card = loader.load();

            // Récupérer le contrôleur du FXML
            EquipeCardController controller = loader.getController();

            // Mettre à jour les éléments du FXML avec les données de l'équipe
            controller.setEquipe(equipe);  // Vous devez implémenter cette méthode dans votre contrôleur

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return new HBox();  // Retourner un HBox vide en cas d'erreur
        }
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
            // Charger le fichier FXML de la page d'ajout d'équipe
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipeadd.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page d'ajout
            EquipeCreateController controller = loader.getController();

            // Initialiser les données nécessaires (constructeurs, gestionnaires, artisans)
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
            List<Constructeur> constructeurs = utilisateurDAO.getAllConstructeurIds().stream()
                    .map(id -> {
                        try {
                            return utilisateurDAO.getConstructeurId(id);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            List<GestionnaireDeStock> gestionnaires = utilisateurDAO.getAllGestionnaireStockIds().stream()
                    .map(id -> utilisateurDAO.getGestionnaireStockId(id))
                    .collect(Collectors.toList());
            List<Artisan> artisans = utilisateurDAO.getAllArtisanIds().stream()
                    .map(id -> {
                        Artisan a = utilisateurDAO.getArtisanId(id);
                        if (a == null) {
                        }
                        return a;
                    })
                    .filter(Objects::nonNull) // Filtre crucial ici
                    .collect(Collectors.toList());

            if (artisans.isEmpty()) {
                showErrorAlert("Erreur Critique", "Aucun artisan valide trouvé !");
                return;
            }

            // Passer les données au contrôleur
            controller.initData(constructeurs, gestionnaires,artisans);

            // Rafraîchir la liste des équipes après la création
            controller.setRefreshCallback(this::refreshEquipeList);


            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Créer une nouvelle équipe");
            stage.showAndWait();

        } catch (IOException | SQLException e) {
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir la page d'ajout d'équipe :\n" + e.getMessage());
        }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}