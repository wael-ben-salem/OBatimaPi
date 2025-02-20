package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
public class UserCardController implements Initializable {

    @FXML private ImageView userImage;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML
    private HBox cardRoot;
    @FXML private ImageView statusIcon;

    private UserListController userListController;


    private Utilisateur user;

    public void setUserData(Utilisateur user) {
        this.user = user;
        updateUI();
        loadImage(user);
    }
    public void setUserListController(UserListController userListController) {
        this.userListController = userListController;
    }

    private void updateUI() {
        nameLabel.setText(user.getNom() + " " + user.getPrenom());
        emailLabel.setText(user.getEmail());
        roleLabel.setText(user.getRole().toString());
        if (user.isConfirmed()) {
            // L'utilisateur est confirmé, afficher l'icône "validate"
            Image validateIcon = new Image(getClass().getResourceAsStream("/images/verified.png"));
            statusIcon.setImage(validateIcon);
        } else {
            // L'utilisateur n'est pas confirmé, afficher l'icône "disabled"
            Image disabledIcon = new Image(getClass().getResourceAsStream("/images/disabled.png"));
            statusIcon.setImage(disabledIcon);
        }
        // Charger l'image personnalisée si disponible
        if(user.getNom() != null && !user.getNom().isEmpty()) {
            try {
                String imagePath = user.getNom() != null ?
                        user.getNom() : "/ourbatima/style/avatars/man1@50.png";
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                userImage.setImage(image);

            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            }
        }



    }
    @FXML
    private void updateUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/utilisateur/userupdateList.fxml"));
            Parent root = loader.load();

            UserUpdateController controller = loader.getController();
            controller.initData(user);
            controller.setUserListController(userListController); // Passer le contrôleur principal

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger la carte après mise à jour
            updateUI();
            if (userListController != null) {
                userListController.loadUsers();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteUser() throws SQLException {
        boolean confirmed = showConfirmationDialog("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        if (confirmed) {
            UtilisateurDAO userDao = new UtilisateurDAO();
            userDao.deleteUser(user.getId());
            if (userListController != null) {
                userListController.loadUsers();
            }
        }
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
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
            userImage.setImage(image);
            userImage.setClip(new Circle(50, 50, 50));

        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image : " + e.getMessage());
            try {
                // Fallback ultime
                Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
                userImage.setImage(defaultImage);
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
    private void showDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/utilisateur/userdetails.fxml"));
            Parent root = loader.load();

            UserDetailsController controller = loader.getController();
            controller.initData(user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration initiale de l'image
        userImage.setClip(new Circle(40, 40, 40)); // Rend l'image circulaire

    }
}