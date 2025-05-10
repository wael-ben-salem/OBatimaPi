package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.controllers.SessionManager;
import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.Contrat;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ContatsClient extends ActionView {
    @FXML
    private Button nextPageButton;
    @FXML private Button previousPageButton;
    @FXML
    private FlowPane ContratContainer;
    private final int pageSize = 9; // Nombre d'utilisateurs par page
    private Context context; // <-- Ajouter cette ligne

    private ContratServise cs =new ContratServise();
    private UtilisateurDAO us =new UtilisateurDAO();
    private int currentPage = 0;

    private int totalabo = 0;
    public void onInit(Context context) {
        this.context = context;
        try {
            loadContrats();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void loadContrats() throws SQLException {
        ContratContainer.getChildren().clear();
        Utilisateur clt= SessionManager.getUtilisateur();
        List <Contrat> contratstot=cs.getContratsbyidclient(clt.getId());
        totalabo = contratstot.size();  // Stocker le nombre total d'utilisateurs pour le rôle
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, totalabo);
        if (start >= totalabo) {
            currentPage = Math.max(0, (totalabo - 1) / pageSize); // Revenir à la dernière page disponible
            start = currentPage * pageSize;
            end = Math.min(start + pageSize, totalabo);
        }
        List<Contrat> pageUsers = contratstot.subList(start, end);

        for (Contrat user : pageUsers) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/CardContrat.fxml"));
                HBox card = loader.load();

                CardContrat controller = loader.getController();
                controller.setUserData(user);
                controller.setListAbonnemant(this);

                ContratContainer.getChildren().add(card);
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


    @FXML
    private void previousPage() throws SQLException {
        if (currentPage > 0) {
            currentPage--;
            loadContrats();
        }
    }

    @FXML
    private void nextPage() throws SQLException {
        if ((currentPage + 1) * pageSize < totalabo) {
            currentPage++;
            loadContrats();
        }
    }

}