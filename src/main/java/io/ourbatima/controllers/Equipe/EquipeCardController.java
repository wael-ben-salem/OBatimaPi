package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Constructeur;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Utilisateur.GestionnaireDeStock;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EquipeCardController extends ActionView implements Initializable {

    // Ajouter ces nouvelles références FXML
    @FXML private FlowPane artisansFlowPane;
    @FXML private Label constructeurLabel;

    @FXML private HBox ratingStars; // Ajoutez cette référence FXML

    @FXML private Button updateButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Label gestionnaireLabel;
    @FXML private Label teamNameLabel;
    @FXML private ImageView teamLogo;

    private Equipe equipe;
    private Runnable refreshParent;

    private void updateUI() {
        teamNameLabel.setText(equipe.getNom());
        constructeurLabel.setText("Constructeur: " + equipe.getConstructeur().getNom());
        gestionnaireLabel.setText("Gestionnaire: " + equipe.getGestionnaireStock().getNom());
        updateArtisansDisplay();
        loadLogo(equipe); // Logo de l'équipe si nécessaire

        updateArtisansDisplay();
        loadLogo(equipe);

        updateRatingStars();


    }

    private void updateRatingStars() {
        ratingStars.getChildren().clear(); // Effacez les étoiles existantes

        double avgRating = equipe.getAverageRating();
        int fullStars = (int) avgRating; // Nombre d'étoiles pleines
        boolean hasHalfStar = (avgRating - fullStars) >= 0.5; // Vérifiez s'il y a une demi-étoile

        // Ajoutez les étoiles pleines
        for (int i = 0; i < fullStars; i++) {
            ratingStars.getChildren().add(createStarImageView("/images/star_filled.png"));
        }

        // Ajoutez une demi-étoile si nécessaire
        if (hasHalfStar) {
            ratingStars.getChildren().add(createStarImageView("/images/star_half.png"));
        }

        // Ajoutez les étoiles vides
        int remainingStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < remainingStars; i++) {
            ratingStars.getChildren().add(createStarImageView("/images/star_empty.png"));
        }
    }
    private ImageView createStarImageView(String imagePath) {
        ImageView star = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        star.setFitWidth(16); // Taille des étoiles
        star.setFitHeight(16);
        return star;
    }


    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Image non trouvée: " + path);
            return new Image(getClass().getResourceAsStream("/images/default.png"));
        }
    }


    public void setRefreshParent(Runnable refreshParent) {
        this.refreshParent = refreshParent;
    }
    @FXML
    private void updateEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/equipe/equipeupdate.fxml"));
            Parent root = loader.load();

            EquipeUpdateController controller = loader.getController();
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

            // Chargement optimisé des données
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
                    .map(id -> {
                        return utilisateurDAO.getGestionnaireStockId(id);
                    })
                    .collect(Collectors.toList());

            controller.initData(this.equipe, constructeurs, gestionnaires);

            // Rafraîchissement après sauvegarde
            controller.setRefreshCallback(() -> {
                try {
                    this.equipe = new EquipeDAO().findById(equipe.getId());
                    updateUI();
                    if(refreshParent != null) refreshParent.run();
                } catch (SQLException e) {
                    showErrorAlert("Erreur de rafraîchissement", e.getMessage());
                }
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException | RuntimeException e) {
            showErrorAlert("Erreur de mise à jour", "Erreur lors de la modification :\n" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void deleteEquipe() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'équipe " + equipe.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement cette équipe ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new EquipeDAO().delete(equipe.getId());
                if(refreshParent != null) refreshParent.run();
            } catch (SQLException e) {
                showErrorAlert("Erreur de suppression", e.getMessage());
            }
        }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateArtisansDisplay() {
        artisansFlowPane.getChildren().clear();
        List<Artisan> artisans = equipe.getArtisans();

        // Afficher max 3 artisans + "..."
        int maxToShow = 3;
        for(int i = 0; i < Math.min(artisans.size(), maxToShow); i++) {
            Circle clip = new Circle(15, 15, 15);
            ImageView iv = new ImageView(getArtisanImage(artisans.get(i)));

            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setClip(clip);
            artisansFlowPane.getChildren().add(iv);
        }

        if(artisans.size() > maxToShow) {
            Label moreLabel = new Label("... +" + (artisans.size() - maxToShow));
            moreLabel.setStyle("-fx-text-fill: #666;");
            artisansFlowPane.getChildren().add(moreLabel);
        }
    }

    private Image getArtisanImage(Artisan artisan) {
        try {
            // Exemple de récupération d'image (à adapter)
            return new Image(getClass().getResourceAsStream(
                    "/images/artisans/art" + artisan.getArtisan_id() + ".png"
            ));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/images/artisans/default.png"));
        }
    }

    // Modifier le loadLogo pour les équipes
    private void loadLogo(Equipe equipe) {
        try {
            String logoPath = "/images/equipes/Equipe" + equipe.getId() + ".png";
            try (InputStream is = getClass().getResourceAsStream(logoPath)) {
                if (is != null) {
                    teamLogo.setImage(new Image(is));
                    return;
                }
            }
        } catch (Exception e) {
            // Ne rien faire, passer au default
        }

        try (InputStream defaultStream = getClass().getResourceAsStream("/images/equipes/default.png")) {
            teamLogo.setImage(new Image(defaultStream));
        } catch (Exception e) {
            teamLogo.setVisible(false);
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


    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        updateUI();
    }

}