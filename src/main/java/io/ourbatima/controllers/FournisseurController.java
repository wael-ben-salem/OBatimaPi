package io.ourbatima.controllers;

import io.ourbatima.core.Dao.Stock.FournisseurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Fournisseur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class FournisseurController extends ActionView {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField adresseField;

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    public void goToHome(ActionEvent actionEvent) {
        context.routes().setView("drawer");
    }

    public void handleAddFournisseur(ActionEvent actionEvent) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();

        // Validate inputs
        if (nom.length() < 3) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le nom doit contenir au moins 3 caractères.");
            return;
        }

        if (prenom.length() < 3) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le prénom doit contenir au moins 3 caractères.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "L'email doit être au format valide.");
            return;
        }

        if (telephone.length() != 8 || !telephone.chars().allMatch(Character::isDigit)) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le numéro de téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        if (adresse.length() < 5) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "L'adresse doit contenir au moins 5 caractères.");
            return;
        }

        if (fournisseurDAO.isFournisseurExist(email)) {
            showAlert(AlertType.ERROR, "Erreur", "Un fournisseur avec cet email existe déjà.");
            return;
        }

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom(nom);
        fournisseur.setPrenom(prenom);
        fournisseur.setEmail(email);
        fournisseur.setNumeroDeTelephone(telephone);
        fournisseur.setAdresse(adresse);

        boolean isSaved = fournisseurDAO.saveFournisseur(fournisseur);

        if (isSaved) {
            showAlert(AlertType.INFORMATION, "Succès", "Le fournisseur a été ajouté avec succès.");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout du fournisseur.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();
    }
}