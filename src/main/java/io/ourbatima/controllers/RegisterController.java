package io.ourbatima.controllers;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextInputDialog;
import javafx.collections.FXCollections;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;

public class RegisterController extends ActionView {
    // Champs existants
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private PasswordField motDePasseField;
    @FXML private Hyperlink loginLink;

    // Nouveaux champs de validation
    @FXML private Label nomError;
    @FXML private Label prenomError;
    @FXML private Label telephoneError;
    @FXML private Label adresseError;
    @FXML private Label passwordError;
    @FXML private ComboBox<String> countryCode;

    // Regex patterns
    private static final String NAME_REGEX = "^[a-zA-ZÀ-ÿ\\s\\-']+";
    private static final String PHONE_REGEX = "^(\\+216|00216)?[2459]\\d{7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        // Initialisation du ComboBox des indicatifs
        countryCode.setItems(FXCollections.observableArrayList("+216", "00216"));
        countryCode.getSelectionModel().selectFirst();

        // Validation en temps réel
        setupRealTimeValidation();
    }

    private void setupRealTimeValidation() {
        // Validation du nom/prénom
        nomField.textProperty().addListener((obs, oldVal, newVal) -> validateNameField(newVal, nomError));
        prenomField.textProperty().addListener((obs, oldVal, newVal) -> validateNameField(newVal, prenomError));

        // Validation du téléphone
        telephoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                telephoneField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            validatePhoneNumber();
        });

        // Validation du mot de passe
        motDePasseField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordStrength(newVal);
            validatePassword();
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        resetErrors();
        if (!validateForm()) {
            return;
        }

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = countryCode.getValue() + telephoneField.getText();
        String adresse = adresseField.getText();
        String motDePasse = motDePasseField.getText();

        if (utilisateurDAO.isEmailExist(email)) {
            showAlert("Erreur", "Cet email est déjà utilisé.");
            return;
        }

        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, motDePasseHache,
                telephone, adresse, Utilisateur.Statut.en_attente, false, Utilisateur.Role.Client);

        if (utilisateurDAO.saveUser(utilisateur)) {
            showAlert("Succès", "Compte créé avec succès !");
            goToLogin(event);
        } else {
            showAlert("Erreur", "Échec de la création du compte.");
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        isValid &= validateNameField(nomField.getText(), nomError);
        isValid &= validateNameField(prenomField.getText(), prenomError);
        isValid &= validatePhoneNumber();
        isValid &= validatePassword();
        isValid &= validateAdresse();

        return isValid;
    }

    private boolean validateNameField(String value, Label errorLabel) {
        if (value == null || value.trim().isEmpty()) {
            errorLabel.setText("Ce champ est obligatoire");
            return false;
        }
        if (!value.matches(NAME_REGEX)) {
            errorLabel.setText("Caractères non valides");
            return false;
        }
        errorLabel.setText("");
        return true;
    }

    private boolean validatePhoneNumber() {
        String fullNumber = countryCode.getValue() + telephoneField.getText();
        if (telephoneField.getText().isEmpty()) {
            telephoneError.setText("Ce champ est obligatoire");
            return false;
        }
        if (!fullNumber.matches(PHONE_REGEX)) {
            telephoneError.setText("Format tunisien invalide");
            return false;
        }
        telephoneError.setText("");
        return true;
    }

    private boolean validatePassword() {
        String password = motDePasseField.getText();
        if (password.isEmpty()) {
            passwordError.setText("Ce champ est obligatoire");
            return false;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            passwordError.setText("Majuscule, chiffre et caractère spécial requis");
            return false;
        }
        passwordError.setText("");
        return true;
    }

    private boolean validateAdresse() {
        if (adresseField.getText().isEmpty()) {
            adresseError.setText("Ce champ est obligatoire");
            return false;
        }
        adresseError.setText("");
        return true;
    }

    private void updatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[@$!%*?&#].*")) strength++;

        // Mise à jour de l'affichage visuel
        // Implémentez la logique CSS selon vos besoins
    }

    @FXML
    private void handleMapClick(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sélection d'adresse");
        dialog.setHeaderText("Entrez votre adresse complète (rue, ville, pays)");
        dialog.setContentText("Adresse:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(adresse -> {
            adresseField.setText(adresse);
            validateAdresse();
        });
    }

    private void resetErrors() {
        nomError.setText("");
        prenomError.setText("");
        telephoneError.setText("");
        adresseError.setText("");
        passwordError.setText("");
    }

    // Méthodes existantes conservées
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goToLogin(ActionEvent actionEvent) {
        try {
            context.routes().setView("login");
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger la page de connexion");
        }
    }
}