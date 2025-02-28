package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;

public class UserDetailsController {

    @FXML
    private ImageView detailImage;
    @FXML private Label detailName;
    @FXML private Label detailRole;
    @FXML private Label detailEmail;
    @FXML private Label detailPhone;
    @FXML private Label detailAddress;
    @FXML private Label detailSpecialite;
    @FXML private Label detailSalaire;
    @FXML private VBox specificDetails;

    public void initData(Utilisateur user) {
        // Informations de base
        detailName.setText(user.getNom() + " " + user.getPrenom());
        detailRole.setText(user.getRole().toString());
        detailEmail.setText("Email: " + user.getEmail());
        detailPhone.setText("Téléphone: " + user.getTelephone());
        detailAddress.setText("Adresse: " + user.getAdresse());

        // Gestion des rôles spécifiques
        specificDetails.setVisible(false);
        if(user.getRole() == Utilisateur.Role.Artisan && user.getArtisan() != null) {
            specificDetails.setVisible(true);
            detailSpecialite.setText("Spécialité: " + user.getArtisan().getSpecialite().name());
            detailSalaire.setText("Salaire/h: " + user.getArtisan().getSalaireHeure());
            System.out.println(user.getArtisan().getSalaireHeure());

        }
        else if(user.getRole() == Utilisateur.Role.Constructeur && user.getConstructeur() != null) {
            specificDetails.setVisible(true);
            detailSpecialite.setText("Spécialité: " + user.getConstructeur().getSpecialite());
            detailSalaire.setText("Salaire/h: " + user.getConstructeur().getSalaireHeure());
            System.out.println(user.getConstructeur().getSalaireHeure());
            System.out.println(user.getConstructeur().getSpecialite());

        }

        // Chargement de l'image
        loadImage(user);
    }

    private void loadImage(Utilisateur user) {
        String defaultImagePath = "/ourbatima/style/avatars/man1@50.png";
        String imagePath = user.getNom(); // Supposons que ce soit le bon getter

        try {
            // Vérifie si le chemin est valide
            if (imagePath == null || imagePath.isEmpty() || !isValidImage(imagePath)) {
                imagePath = defaultImagePath;
            }

            // Charge l'image depuis les ressources
            InputStream inputStream = getClass().getResourceAsStream(imagePath);

            // Double vérification si l'image par défaut est manquante
            if (inputStream == null) {
                System.err.println("Image par défaut non trouvée !");
                inputStream = getClass().getResourceAsStream(defaultImagePath);
            }

            Image image = new Image(inputStream);
            detailImage.setImage(image);
            detailImage.setClip(new Circle(50, 50, 50));

        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image : " + e.getMessage());
            try {
                // Fallback ultime
                Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
                detailImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Échec du chargement de l'image par défaut : " + ex.getMessage());
            }
        }
    }

    // Méthode utilitaire pour vérifier l'existence du fichier
    private boolean isValidImage(String path) {
        try {
            return getClass().getResource(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void close() {
        ((Stage) detailName.getScene().getWindow()).close();
    }
}