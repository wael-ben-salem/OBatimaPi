package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.impl.IRoutes;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
public class UserListController extends ActionView implements Initializable {

    @FXML private FlowPane usersContainer;
    @FXML private Label titleLabel;

    private Utilisateur.Role currentRole;
    private final UtilisateurDAO userDao = new UtilisateurDAO();
    private Context context; // <-- Ajouter cette ligne
    private int currentPage = 0;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;
    private int totalUsers = 0; // Nombre total d'utilisateurs pour le rôle courant

    private final int pageSize = 9; // Nombre d'utilisateurs par page
    public void onInit(Context context) {
        this.context = context;
        initializeRole();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Laisser vide si vous initialisez via setContext
    }

    private void initializeRole() {
        usersContainer.setHgap(10);  // Espacement horizontal
        usersContainer.setVgap(10);  // Espacement vertical
        usersContainer.setPrefWrapLength(3 * 400 + 20);
        if (context == null || context.routes() == null) {
            System.err.println("Contexte ou routes non initialisé !");
            return;
        }
        // Récupérer IRoutes depuis le contexte
        IRoutes routes = (IRoutes) context.routes();
        String routeName = routes.getCurrentRouteName();
        if (routeName == null) {
            System.err.println("Nom de la route non défini !");
            return;
        }
        // Déterminer le rôle en fonction de la route
        switch (routeName) {
            case "artisan_list":
                currentRole = Utilisateur.Role.Artisan;
                break;
            case "constructeur_list":
                currentRole = Utilisateur.Role.Constructeur;
                break;
            case "client_list":
                currentRole = Utilisateur.Role.Client;
                break;
            case "gestionnaire_stock_list":
                currentRole = Utilisateur.Role.GestionnaireStock;
                break;
            default:
                currentRole = null;
                break;
        }

        if (currentRole != null) {
            titleLabel.setText(currentRole.name() + "s");
            loadUsers();
        }
    }

    void loadUsers() {
        usersContainer.getChildren().clear();
        List<Utilisateur> users = userDao.getUsersByRole(currentRole);
        totalUsers = users.size();  // Stocker le nombre total d'utilisateurs pour le rôle
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, totalUsers);
        if (start >= totalUsers) {
            currentPage = Math.max(0, (totalUsers - 1) / pageSize); // Revenir à la dernière page disponible
            start = currentPage * pageSize;
            end = Math.min(start + pageSize, totalUsers);
        }
        List<Utilisateur> pageUsers = users.subList(start, end);

        for (Utilisateur user : pageUsers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/utilisateur/usercard.fxml"));
                HBox card = loader.load();

                UserCardController controller = loader.getController();
                controller.setUserData(user);
                controller.setUserListController(this);
                usersContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updatePaginationButtons();

    }

    @FXML
    private void openCreateUserForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/utilisateur/useraddList.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger la liste des utilisateurs après création
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updatePaginationButtons() {
        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable((currentPage + 1) * pageSize >= totalUsers);
    }
    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadUsers();
        }
    }

    @FXML
    private void nextPage() {
        if ((currentPage + 1) * pageSize < totalUsers) {
            currentPage++;
            loadUsers();
        }
    }



}

