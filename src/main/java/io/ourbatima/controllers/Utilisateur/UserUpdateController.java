package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Constructeur;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserUpdateController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<Utilisateur.Role> roleComboBox;
    private UserListController userListController;


    @FXML private VBox specificFields;
    @FXML private TextField specialiteField;
    @FXML private TextField salaireField;
    @FXML private ComboBox<Artisan.Specialite> specialiteComboBox; // Ajout de la ComboBox pour les spécialités

    private Utilisateur user;
    private UtilisateurDAO userDao = new UtilisateurDAO();

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les rôles disponibles
        roleComboBox.getItems().setAll(Utilisateur.Role.values());
        specialiteComboBox.getItems().setAll(Artisan.Specialite.values()); // Remplir la ComboBox des spécialités

        // Masquer les champs spécifiques par défaut
        specificFields.setVisible(false);

        // Écouter les changements de rôle pour afficher les champs spécifiques
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldRole, newRole) -> {
            if (newRole == Utilisateur.Role.Artisan) {
                specificFields.setVisible(true);
                specialiteComboBox.setVisible(true);
                specialiteField.setVisible(false);
            }
            else if (newRole == Utilisateur.Role.Constructeur) {
                specificFields.setVisible(true);
                specialiteComboBox.setVisible(false);
                specialiteField.setVisible(true);
            }
            else {
                specificFields.setVisible(false);
            }
        });
    }
    public void setUserListController(UserListController userListController) {
        this.userListController = userListController;
    }
    public void initData(Utilisateur user) {
        this.user = user;

        // Pré-remplir les champs avec les données de l'utilisateur
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        telephoneField.setText(user.getTelephone());
        adresseField.setText(user.getAdresse());
        roleComboBox.setValue(user.getRole());

        // Afficher les champs spécifiques en fonction du rôle
        if (user.getRole() == Utilisateur.Role.Artisan && user.getArtisan() != null) {
            specificFields.setVisible(true);
            specialiteComboBox.setValue(user.getArtisan().getSpecialite()); // Utiliser la ComboBox pour la spécialité
            salaireField.setText(String.valueOf(user.getArtisan().getSalaireHeure()));
        } else if (user.getRole() == Utilisateur.Role.Constructeur && user.getConstructeur() != null) {
            specificFields.setVisible(true);
            specialiteField.setText(user.getConstructeur().getSpecialite()); // String
            salaireField.setText(String.valueOf(user.getConstructeur().getSalaireHeure()));
        }
    }

    @FXML
    private void updateUser() {
        try {
            // Récupérer les données du formulaire
            user.setNom(nomField.getText());
            user.setPrenom(prenomField.getText());
            user.setEmail(emailField.getText());
            user.setTelephone(telephoneField.getText());
            user.setAdresse(adresseField.getText());
            user.setRole(roleComboBox.getValue());

            // Mettre à jour les champs spécifiques en fonction du rôle
            if (user.getRole() == Utilisateur.Role.Artisan) {
                if (user.getArtisan() == null) {
                    user.setArtisan(new Artisan()); // Initialiser un nouvel Artisan
                }
                user.getArtisan().setSpecialite(specialiteComboBox.getValue()); // Utiliser la ComboBox pour la spécialité
                user.getArtisan().setSalaireHeure(Double.parseDouble(salaireField.getText()));
            } else if (user.getRole() == Utilisateur.Role.Constructeur) {
                if (user.getConstructeur() == null) {
                    user.setConstructeur(new Constructeur()); // Initialiser un nouveau Constructeur
                }
                user.getConstructeur().setSpecialite(specialiteField.getText()); // String
                user.getConstructeur().setSalaireHeure(Double.parseDouble(salaireField.getText()));
            }

            // Mettre à jour l'utilisateur dans la base de données
            boolean success = userDao.updateUser(user);
            if (success) {
                showAlert("Succès", "L'utilisateur a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
                if (userListController != null) {
                    userListController.loadUsers();
                }
                close();
            } else {
                showAlert("Erreur", "Échec de la mise à jour de l'utilisateur.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le salaire doit être un nombre valide.", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", "Spécialité invalide. Veuillez sélectionner une valeur valide.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
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