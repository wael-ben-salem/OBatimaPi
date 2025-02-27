package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.controllers.Utilisateur.UserCardController;
import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListAbonnement extends ActionView {
    @FXML private Button nextPageButton;
    @FXML private Button previousPageButton;
    @FXML
    private FlowPane AbonnementContainer;

    private final int pageSize = 9; // Nombre d'utilisateurs par page
    private Context context; // <-- Ajouter cette ligne

    private abonnementService as =new abonnementService();
    private int currentPage = 0;

    private int totalabo = 0;



    public void onInit(Context context) {
        this.context = context;
        loadUsers();
    }


    void loadUsers() {
        AbonnementContainer.getChildren().clear();
        List<abonnement> abo = as.getAllAbonnement();
        System.out.println(abo);
        totalabo = abo.size();  // Stocker le nombre total d'utilisateurs pour le rôle
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, totalabo);
        if (start >= totalabo) {
            currentPage = Math.max(0, (totalabo - 1) / pageSize); // Revenir à la dernière page disponible
            start = currentPage * pageSize;
            end = Math.min(start + pageSize, totalabo);
        }
        List<abonnement> pageUsers = abo.subList(start, end);

        for (abonnement user : pageUsers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/cardAbonnement.fxml"));
                HBox card = loader.load();

                cardAbonnement controller = loader.getController();
                controller.setUserData(user);
                controller.setListAbonnemant(this);

                AbonnementContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updatePaginationButtons();

    }


        private void updatePaginationButtons() {
            previousPageButton.setDisable(currentPage == 0);
            nextPageButton.setDisable((currentPage + 1) * pageSize >= totalabo);
        }



    public void openCreateUserForm(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/Finance_vews/CreateAbonnement.fxml"));
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


    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadUsers();
        }
    }

    @FXML
    private void nextPage() {
        if ((currentPage + 1) * pageSize < totalabo) {
            currentPage++;
            loadUsers();
        }
    }


}
