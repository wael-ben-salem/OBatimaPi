package io.ourbatima.controllers.Equipe;

import io.ourbatima.controllers.MessagingService;
import io.ourbatima.controllers.SessionManager;
import io.ourbatima.core.Dao.Utilisateur.TeamRatingDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.*;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class EquipeDetailController extends ActionView {
    private Equipe currentEquipe; // Nouveau champ

    @FXML private HBox ratingContainer;
    @FXML private Label averageRatingLabel;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private Button submitRatingButton;

    @FXML private ImageView teamLogo;
    @FXML private Label teamName;
    @FXML private ImageView constructeurImage;
    @FXML private Label constructeurLabel;
    @FXML private ImageView gestionnaireImage;
    @FXML private Label gestionnaireLabel;
    @FXML private Label dateCreation;
    @FXML private FlowPane artisansFlowPane;

    private final Map<Integer, Image> constructeurImageCache = new HashMap<>();
    private final Map<Integer, Image> gestionnaireImageCache = new HashMap<>();
    public void initData(Equipe equipe) {
        if (equipe == null) {
            throw new IllegalArgumentException("L'équipe ne peut pas être null.");
        }

        this.currentEquipe = equipe; // Initialisez currentEquipe en premier

        // Initialisez les autres données
        teamName.setText(equipe.getNom());
        constructeurLabel.setText(equipe.getConstructeur().getNom());
        gestionnaireLabel.setText(equipe.getGestionnaireStock().getNom());
        dateCreation.setText(equipe.getDateCreation().format(DateTimeFormatter.ISO_DATE));

        // Chargez les images
        loadConstructeurImage(equipe.getConstructeur());
        loadGestionnaireImage(equipe.getGestionnaireStock());
        loadArtisansImages(equipe);
        loadTeamLogo(equipe.getId());

        // Mettez à jour l'affichage de la note
        updateRatingDisplay(); // Déplacez cet appel après l'initialisation de currentEquipe
        setupRatingUI();
    }

    private void updateRatingDisplay() {
        double avg = currentEquipe.getAverageRating();
        averageRatingLabel.setText(String.format("Note moyenne : %.1f/5", avg));
    }

    private void sendRatingNotifications(int rating) throws SQLException {
        String message = String.format("Le client %s a noté l'équipe avec %d/5",
                SessionManager.getUtilisateur().getNom(), rating);

        MessagingService messagingService = new MessagingService();
        for (Utilisateur membre : currentEquipe.getMembres()) {
            messagingService.sendTeamNotification(currentEquipe, message);
        }
    }
    private void setupRatingUI() {
        Utilisateur user = SessionManager.getUtilisateur();
        if (user != null && user.getRole() == Utilisateur.Role.Client) {
            // Vérifiez si le client a déjà noté cette équipe
            try {
                boolean hasRated = new TeamRatingDAO().hasClientRated(user.getId(), currentEquipe.getId());
                if (hasRated) {
                    ratingComboBox.setDisable(true); // Désactivez la ComboBox
                    submitRatingButton.setDisable(true); // Désactivez le bouton
                    submitRatingButton.setText("Déjà noté");
                } else {
                    ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
                    ratingComboBox.setVisible(true);
                    submitRatingButton.setVisible(true);
                }
            } catch (SQLException e) {
                showErrorAlert("Erreur", "Impossible de vérifier la notation.");
            }
        }
    }
    @FXML
    private void handleRatingSubmission() {
        Integer rating = ratingComboBox.getValue();
        if (rating == null) {
            showErrorAlert("Erreur", "Veuillez sélectionner une note");
            return;
        }

        TeamRating teamRating = new TeamRating();
        teamRating.setEquipe(currentEquipe);
        teamRating.setClient(SessionManager.getUtilisateur());
        teamRating.setRating(rating);

        try {
            new TeamRatingDAO().saveRating(teamRating);
            sendRatingNotifications(rating);
            updateRatingDisplay();

            // Désactivez le bouton et la ComboBox après la notation
            ratingComboBox.setDisable(true);
            submitRatingButton.setDisable(true);
            submitRatingButton.setText("Déjà noté");
        } catch (SQLException e) {
            showErrorAlert("Erreur", "Échec de l'enregistrement");
        }
    }
    private void loadConstructeurImage(Constructeur constructeur) {
        Image image = loadImage(
                constructeur.getConstructeur_id(),
                "constructeurs",
                "con",
                constructeurImageCache
        );
        applyImageEffect(constructeurImage, image);
    }

    private void loadGestionnaireImage(GestionnaireDeStock gestionnaire) {
        Image image = loadImage(
                gestionnaire.getGestionnairestock_id(),
                "gestionnaires",
                "ges",
                gestionnaireImageCache
        );
        applyImageEffect(gestionnaireImage, image);
    }

    private Image loadImage(int id, String folder, String prefix, Map<Integer, Image> cache) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        String[] extensions = {"png", "jpg", "jpeg"};
        for (String ext : extensions) {
            String path = String.format("/images/%s/%s%d.%s", folder, prefix, id, ext);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    Image image = new Image(is);
                    cache.put(id, image);
                    return image;
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement: " + path);
            }
        }

        // Fallback sur l'image par défaut
        Image defaultImage = loadDefaultImage(folder);
        cache.put(id, defaultImage);
        return defaultImage;
    }

    private Image loadDefaultImage(String folder) {
        String path = String.format("/images/%s/default.png", folder);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    private void applyImageEffect(ImageView imageView, Image image) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), imageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        imageView.setImage(image);
        fadeIn.play();
    }

    private void loadArtisansImages(Equipe equipe) {
        artisansFlowPane.getChildren().clear();

        equipe.getArtisans().forEach(artisan -> {
            VBox artisanBox = new VBox(5);
            artisanBox.setStyle("-fx-alignment: center;");

            ImageView iv = new ImageView(getArtisanImage(artisan));
            iv.setFitWidth(60);
            iv.setFitHeight(60);

            Label nameLabel = new Label(artisan.getNom());
            nameLabel.setStyle("-fx-font-size: 12px;");

            artisanBox.getChildren().addAll(iv, nameLabel);
            artisansFlowPane.getChildren().add(artisanBox);
        });
    }

    private Image getArtisanImage(Artisan artisan) {
        String path = String.format("/images/artisans/art%d.png", artisan.getArtisan_id());
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/images/artisans/default.png"));
        }
    }

    private void loadTeamLogo(int equipeId) {
        String[] extensions = {"png", "jpg", "jpeg"};
        for (String ext : extensions) {
            String path = String.format("/images/equipes/Equipe%d.%s", equipeId, ext);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    teamLogo.setImage(new Image(is));
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement logo: " + path);
            }
        }

        // Chargement sécurisé de l'image par défaut
        try (InputStream defaultStream = getClass().getResourceAsStream("/images/equipes/default.png")) {
            teamLogo.setImage(new Image(defaultStream));
        } catch (Exception e) {
            System.err.println("Image par défaut manquante !");
            teamLogo.setImage(null); // Désactive l'image si rien n'est trouvé
        }
    }




    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Image loadImage(String path) {
        // Chemin corrigé avec vérification de null
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Image introuvable : " + path);
            return new Image(getClass().getResourceAsStream("/images/default.png")); // Image de secours
        }
        return new Image(is);
    }


}