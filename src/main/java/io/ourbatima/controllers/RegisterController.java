package io.ourbatima.controllers;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayInputStream;

public class RegisterController extends ActionView {
    // Champs existants
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    private byte[] capturedFaceData;

    @FXML
    private ImageView facePreview;
    @FXML
    private Button btnEnrollFace;
    @FXML private TextField emailField;
    private Utilisateur utilisateur;

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
        if (capturedFaceData == null) { // <-- Ajouter cette vérification
            System.out.println("Veuillez capturer un visage avant de sauvegarder");
            return;
        }

        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, motDePasseHache,
                telephone, adresse, Utilisateur.Statut.en_attente, false, Utilisateur.Role.Client);
        utilisateur.setFaceData(capturedFaceData);
        if (utilisateurDAO.saveUser(utilisateur)) {
            sendWelcomeEmail();
            showAlert("Succès", "Compte créé avec succès !");
            goToLogin(event);
        } else {
            showAlert("Erreur", "Échec de la création du compte.");
        }
    }

    private void sendWelcomeEmail() {
        new Thread(() -> {
            try {
                String message = "Bienvenue sur ourbatima!\n\nVos identifiants:\nEmail: "
                        + utilisateur.getEmail();

                System.out.println("Tentative d'envoi à " + utilisateur.getEmail()); // Debug
                EmailService.sendEmail(
                        utilisateur.getEmail(),
                        "Confirmation de compte",
                        message
                );

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert(String.valueOf(AlertType.ERROR),
                            "Erreur technique: " + e.getCause().getMessage());
                });
            }
        }).start();
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
        System.out.println("📍 Ouverture de la carte...");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", new JSBridge(this));

                requestLocationPermission(webEngine);
                System.out.println("✅ Bridge JS-Java initialisé");
            }
        });

        webEngine.load(getClass().getResource("/api/map.html").toExternalForm());

        mapStage = new Stage();
        mapStage.setScene(new Scene(webView));
        mapStage.setTitle("Sélection de position");
        mapStage.show();
    }
    private Stage mapStage;

    // Demande l'autorisation de localisation et exécute le JS
    private void requestLocationPermission(WebEngine webEngine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Autorisation de localisation");
        alert.setHeaderText("L'application souhaite accéder à votre position.");
        alert.setContentText("Autoriser la géolocalisation ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("🌍 Autorisation accordée, récupération de la position...");
                webEngine.executeScript("locateUser();");
            } else {
                System.out.println("🚫 Autorisation refusée !");
            }
        });
    }

    // Met à jour le champ adresse avec l'adresse reçue depuis JS
    public void updateAddressField(String address) {
        System.out.println("📨 Adresse reçue : " + address);

        Platform.runLater(() -> {
            adresseField.setText(address);
            validateAdresse();
            if (mapStage != null) mapStage.close();
        });
    }


    @FXML
    private void handleFaceEnrollment() {
        // Créer une alerte de progression
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Capture faciale");
        progressAlert.setHeaderText("Capture en cours...");
        progressAlert.setContentText("Veuillez positionner votre visage face à la caméra");
        progressAlert.initOwner(btnEnrollFace.getScene().getWindow());

        // Ajouter un indicateur de progression
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressAlert.setGraphic(progressIndicator);
        progressAlert.show();

        btnEnrollFace.setDisable(true);

        new Thread(() -> {
            try {
                byte[] rawData = FaceAuthenticator.captureFace();
                if (rawData == null) {
                    Platform.runLater(() -> {
                        progressAlert.close();
                        showAlertplus(Alert.AlertType.WARNING,
                                "Aucun visage détecté",
                                "Veuillez vous positionner correctement devant la caméra");
                    });
                    return;
                }

                byte[] encryptedData = FaceEncryption.encrypt(rawData);
                byte[] decryptedData = FaceEncryption.decrypt(encryptedData);

                Platform.runLater(() -> {
                    progressAlert.close();
                    facePreview.setImage(new Image(new ByteArrayInputStream(decryptedData)));
                    capturedFaceData = encryptedData;
                    showAlertplus(Alert.AlertType.INFORMATION,
                            "Succès",
                            "Capture faciale terminée avec succès !");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressAlert.close();
                    showAlertplus(Alert.AlertType.ERROR,
                            "Erreur",
                            "Échec de la capture : " + e.getMessage());
                });
            } finally {
                Platform.runLater(() -> btnEnrollFace.setDisable(false));
            }
        }).start();
    }

    // Bridge pour la communication entre Java et JavaScript
    public class JSBridge {
        private final RegisterController controller;

        public JSBridge(RegisterController controller) {
            this.controller = controller;
        }

        public void sendAddress(String address) {
            System.out.println("📨 Adresse reçue depuis JS : " + address);
            controller.updateAddressField(address);
        }
    }

    private void showAlertplus(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(btnEnrollFace.getScene().getWindow());
            alert.showAndWait();
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