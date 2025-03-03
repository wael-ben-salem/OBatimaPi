package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class UserCreateController {
    @FXML private TextField nomField;
    @FXML
    private TextField prenomField;

    @FXML
    private PasswordField motDePasseField;

    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<Utilisateur.Role> roleComboBox;

    @FXML private ComboBox<Artisan.Specialite> SpecialiteBox;

    @FXML private VBox specificDetails;
    @FXML private TextField specialiteField;
    @FXML private TextField salaireField;
    private UtilisateurDAO userDao = new UtilisateurDAO();

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les rôles disponibles
        roleComboBox.getItems().setAll(Utilisateur.Role.values());
        SpecialiteBox.getItems().setAll(Artisan.Specialite.values());

        roleComboBox.valueProperty().addListener((obs, oldRole, newRole) -> updateSpecificDetails(newRole));

        specificDetails.setVisible(false);
        SpecialiteBox.setVisible(false);
        specialiteField.setVisible(false);
    }

    private void updateSpecificDetails(Utilisateur.Role role) {
        if (role == Utilisateur.Role.Artisan) {
            specificDetails.setVisible(true);
            SpecialiteBox.setVisible(true);
            specialiteField.setVisible(false);
        } else if (role == Utilisateur.Role.Constructeur) {
            specificDetails.setVisible(true);
            SpecialiteBox.setVisible(false);
            specialiteField.setVisible(true);
        } else {
            specificDetails.setVisible(false);
        }
    }

    @FXML
    private void createUser() {
        // Récupération des champs
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String motDePasse = motDePasseField.getText();
        Utilisateur.Role role = roleComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || motDePasse.isEmpty() || role == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Créer l'utilisateur de base
        Utilisateur newUser = new Utilisateur();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email);
        newUser.setTelephone(telephone);
        newUser.setAdresse(adresse);
        newUser.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt())); // Hash du mot de passe
        newUser.setRole(role);
        newUser.setStatut(Utilisateur.Statut.en_attente);
        newUser.setConfirmed(true);

        // Gestion des rôles spécifiques
        try {
            if (role == Utilisateur.Role.Artisan) {
                Artisan.Specialite artisanSpecialite = SpecialiteBox.getValue();
                double salaire = Double.parseDouble(salaireField.getText());

                if (artisanSpecialite == null) {
                    showAlert("Erreur", "Veuillez sélectionner une spécialité.");
                    return;
                }

                // Créer un Artisan avec les détails spécifiques
                Artisan artisan = new Artisan();
                artisan.setSpecialite(artisanSpecialite);
                artisan.setSalaireHeure(salaire);
                newUser.setArtisan(artisan); // Attacher l'artisan à l'utilisateur

            } else if (role == Utilisateur.Role.Constructeur) {
                String specialite = specialiteField.getText();
                double salaire = Double.parseDouble(salaireField.getText());

                if (specialite.isEmpty()) {
                    showAlert("Erreur", "Veuillez entrer une spécialité.");
                    return;
                }

                // Créer un Constructeur avec les détails spécifiques
                Constructeur constructeur = new Constructeur();
                constructeur.setSpecialite(specialite);
                constructeur.setSalaireHeure(salaire);
                newUser.setConstructeur(constructeur); // Attacher le constructeur à l'utilisateur
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format de salaire invalide.");
            return;
        }

        // Sauvegarde en base de données
        boolean success = userDao.saveUser(newUser);

        if (success) {
            showAlert("Succès", "Utilisateur créé avec succès.");
            close();
        } else {
            showAlert("Erreur", "Échec de la création de l'utilisateur.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void close() {
        ((Stage) nomField.getScene().getWindow()).close();
    }
}
