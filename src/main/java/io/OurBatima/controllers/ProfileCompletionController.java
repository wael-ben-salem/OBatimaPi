package io.OurBatima.controllers;

import io.OurBatima.core.Context;
import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Utilisateur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class ProfileCompletionController extends ActionView {
    @FXML
    private TextField txtNom;

    private Runnable onCompletionSuccess; // Changement ici

    @FXML private TextField txtPrenom;
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

        // Force l'initialisation UI
        Platform.runLater(this::initFields);
    }

    @FXML
    private void onSave() {
        if (!validateForm()) return;

        updateUserFromForm();
        try {
            boolean success = dao.updateUser(utilisateur);

            if (success) {
                SessionManager.setUtilisateur(utilisateur); // Ajoutez cette ligne

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
    private boolean validateForm() {
        if (txtPassword.getText().length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Le mot de passe doit contenir 8 caractères minimum");
            return false;
        }
        return true;
    }

    private void updateUserFromForm() {
        utilisateur.setTelephone(txtTelephone.getText());
        utilisateur.setAdresse(txtAdresse.getText());
        utilisateur.setMotDePasse(BCrypt.hashpw(txtPassword.getText(), BCrypt.gensalt()));
    }






    private void sendWelcomeEmail() {
        new Thread(() -> {
            try {
                String message = "Bienvenue sur OurBatima!\n\nVos identifiants:\nEmail: "
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




}
