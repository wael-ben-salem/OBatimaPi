package io.OurBatima.controllers;

import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController extends ActionView {
    @FXML
    private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private PasswordField motDePasseField;
    @FXML private Hyperlink loginLink;

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO(); // Instance du DAO

    // Cette méthode sera appelée lorsque l'utilisateur cliquera sur "S'inscrire"
    @FXML
    private void handleRegister(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String motDePasse = motDePasseField.getText();

        // Validation des champs
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || motDePasse.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérification de l'email unique dans la base de données via le DAO
        if (utilisateurDAO.isEmailExist(email)) {
            showAlert("Erreur", "Cet email est déjà utilisé.");
            return;
        }

        // Hachage du mot de passe avec BCrypt
        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());

        // Créer un objet Utilisateur
        Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, motDePasseHache, telephone, adresse, "désactivé", false, "USER");

        // Appeler la méthode du DAO pour enregistrer l'utilisateur dans la base de données
        boolean isSaved = utilisateurDAO.saveUser(utilisateur);

        if (isSaved) {
            showAlert("Succès", "Votre compte a été créé avec succès.");
            // Rediriger vers la page de connexion
            goToLogin(event);
        } else {
            showAlert("Erreur", "Une erreur est survenue lors de la création de votre compte.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour rediriger vers la page de connexion
    @FXML

    public void goToLogin(javafx.event.ActionEvent actionEvent) {
        try {
            context.routes().setView("login"); // Navigation vers la vue 'register' définie dans views.yml
            System.out.println("Navigation vers la page d'inscription réussie !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation vers la page d'inscription : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
