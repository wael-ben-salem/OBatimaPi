package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import javafx.event.ActionEvent;

public class ProfileCompletionController extends ActionView {
    @FXML
    private TextField txtNom;

    private Runnable onCompletionSuccess; // Changement ici

    @FXML private TextField txtPrenom;
    @FXML private ComboBox<String> cbCountryCode;
    @FXML private Label telephoneError;
    @FXML private Label adresseError;
    @FXML private Label passwordError;

    private static final String PHONE_REGEX = "^(\\+216|00216)?[2459]\\d{7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;

    @FXML private TextField txtAdresse;
    @FXML private PasswordField txtPassword;
    private OnSaveListener onSaveListener; // Interface de rappel

    @FXML private StackPane mainPane;



    private Utilisateur utilisateur;
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    private Context context;

    private final UtilisateurDAO dao = new UtilisateurDAO();


    public void setUser(Utilisateur user) {
        this.utilisateur = user;
        initFields();
    }
    public interface OnSaveListener {
        void onSaveSuccess(Utilisateur updatedUser);
    }

    private void initFields() {
        txtNom.setText(utilisateur.getNom());
        txtPrenom.setText(utilisateur.getPrenom());
        txtEmail.setText(utilisateur.getEmail());
        txtEmail.setEditable(false);
    }
    public void setOnCompletionSuccess(Runnable callback) {
        this.onCompletionSuccess = callback;
    }
    public void onEnter() {
        // Récupérer l'utilisateur depuis le contexte
        this.utilisateur = (Utilisateur) context.getProperty("currentUser");
        if(utilisateur == null) {
            showAlert(Alert.AlertType.ERROR, "Session invalide !");
            context.routes().nav("login");
            return;
        }

        setupValidations();
        cbCountryCode.getSelectionModel().selectFirst();


        // Force l'initialisation UI
        Platform.runLater(this::initFields);
    }

    private void setupValidations() {
        txtTelephone.textProperty().addListener((obs, oldVal, newVal) -> validatePhone());
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> validatePassword());
    }

    private boolean validateForm() {
        boolean isValid = true;

        isValid &= validatePhone();
        isValid &= validateAddress();
        isValid &= validatePassword();

        return isValid;
    }


    private boolean validatePhone() {
        String phone = cbCountryCode.getValue() + txtTelephone.getText();
        if (txtTelephone.getText().isEmpty()) {
            telephoneError.setText("Ce champ est obligatoire");
            return false;
        }
        if (!phone.matches(PHONE_REGEX)) {
            telephoneError.setText("Format tunisien invalide");
            return false;
        }
        telephoneError.setText("");
        return true;
    }

    private boolean validateAddress() {
        if (txtAdresse.getText().trim().isEmpty()) {
            adresseError.setText("Veuillez saisir une adresse valide");
            return false;
        }
        adresseError.setText("");
        return true;
    }

    private boolean validatePassword() {
        String password = txtPassword.getText();
        if (password.isEmpty()) {
            passwordError.setText("Ce champ est obligatoire");
            return false;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            passwordError.setText("8 caractères min, 1 majuscule, 1 chiffre");
            return false;
        }
        passwordError.setText("");
        return true;
    }

    @FXML
    private void onSave() {
        if (!validateForm()) return;

        updateUserFromForm();
        try {
            boolean success = dao.updateUser(utilisateur);

            if (success) {
                SessionManager.getInstance().startSession(utilisateur); // Ajoutez cette ligne

                sendWelcomeEmail();

                // Fermer la popup d'abord
                Stage popupStage = (Stage) txtNom.getScene().getWindow();
                popupStage.close();

                // Mettre à jour l'interface principale DANS LE MAIN CONTROLLER
                if (onCompletionSuccess != null) {
                    onCompletionSuccess.run(); // Déclencher la navigation
                }
            }
        } catch (Exception e) {
            showError("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    public Utilisateur getUpdatedUser() {
        return utilisateur;
    }


    private void updateUserFromForm() {
        utilisateur.setTelephone(txtTelephone.getText());
        utilisateur.setAdresse(txtAdresse.getText());
        utilisateur.setMotDePasse(BCrypt.hashpw(txtPassword.getText(), BCrypt.gensalt()));
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
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur technique: " + e.getCause().getMessage());
                });
            }
        }).start();
    }
    public void setContext(Context context) {
        this.context = context;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    private Stage mapStage;

    @FXML
    private void handleMapClick(ActionEvent event) {
        System.out.println("📍 Ouverture de la carte...");
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", new JSBridge(this)); // Permet à JS d'appeler Java

                // Demande l'autorisation via une alerte JavaFX
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

    // Demander l'autorisation de localisation et exécuter le JS
    private void requestLocationPermission(WebEngine webEngine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Autorisation de localisation");
        alert.setHeaderText("L'application souhaite accéder à votre position.");
        alert.setContentText("Autoriser la géolocalisation ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("🌍 Autorisation accordée, récupération de la position...");
                webEngine.executeScript("locateUser();"); // Exécute la fonction JS
            } else {
                System.out.println("🚫 Autorisation refusée !");
            }
        });
    }

    public void updateAddressField(String address) {
        System.out.println("📨 Adresse reçue : " + address);

        Platform.runLater(() -> {
            if (txtAdresse != null) {
                txtAdresse.setText(address);
                System.out.println("✅ Adresse mise à jour : " + txtAdresse.getText());
            } else {
                System.out.println("❌ ERREUR : txtAdresse est NULL lors de la mise à jour !");
            }
            if (mapStage != null) mapStage.close();
        });
    }

    public class JSBridge {
        private final ProfileCompletionController controller;

        public JSBridge(ProfileCompletionController controller) {
            this.controller = controller;
        }
        public void receiveLocation(double latitude, double longitude) {
            System.out.println("📌 Position reçue : Latitude = " + latitude + ", Longitude = " + longitude);
            controller.updateLocation(latitude, longitude);
        }

        public void sendAddress(String address) {
            System.out.println("📨 Adresse reçue depuis JS : " + address);
            controller.updateAddressField(address);
        }
    }
    public void updateLocation(double latitude, double longitude) {
        Platform.runLater(() -> {
            txtAdresse.setText("Lat: " + latitude + ", Lng: " + longitude);
        });
    }

}
