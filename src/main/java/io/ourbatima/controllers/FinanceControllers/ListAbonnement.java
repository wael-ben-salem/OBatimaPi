package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.controllers.SessionManager;
import io.ourbatima.controllers.Utilisateur.UserCardController;
import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListAbonnement extends ActionView {
    public Button crreatebuton;
    public ToolBar top;
    public BorderPane ala;
    @FXML private Button nextPageButton;
    @FXML private Button previousPageButton;
    @FXML
    private FlowPane AbonnementContainer;
    @FXML private TextField searchField;
    @FXML private ListView<String> suggestionsAbonnementList;

    private final int pageSize = 9;
    private Context context;

    private abonnementService as =new abonnementService();
    private int currentPage = 0;
    private ObservableList<abonnement> abonnements = FXCollections.observableArrayList();

    private int totalabo = 0;



    public void onInit(Context context) {
        this.context = context;
        if (SessionManager.getUtilisateur().getRole()== Utilisateur.Role.Client){
            ala.getChildren().remove(crreatebuton);
            System.out.println("aaaaaaaaa^saepzl;d");
        }
        loadUsers();
        loadAbonnements();
        setupSearchListener();
    }

    private void loadAbonnements() {
        abonnements.setAll(as.getAllAbonnement());
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                loadUsers();
            } else {
                filterAbonnements(newValue);
            }
        });

        suggestionsAbonnementList.setOnMouseClicked(event -> {
            String selectedName = suggestionsAbonnementList.getSelectionModel().getSelectedItem();
            if (selectedName != null) {
                searchField.setText(selectedName);
                showAbonnementDetails(selectedName);
                suggestionsAbonnementList.getItems().clear();
            }
        });
    }



    private void filterAbonnements(String query) {
        if (query.isEmpty()) {
            suggestionsAbonnementList.getItems().clear();
            return;
        }

        List<String> filteredNames = abonnements.stream()
                .map(abonnement::getNomAbonnement)
                .filter(name -> name.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        suggestionsAbonnementList.setItems(FXCollections.observableArrayList(filteredNames));
    }
    private void showAbonnementDetails(String name) {
        AbonnementContainer.getChildren().clear(); // Clear previous cards

        // Ensure we match by the correct field (NomAbonnement)
        abonnements.stream()
                .filter(abo -> abo.getNomAbonnement().equalsIgnoreCase(name))  // case-insensitive comparison
                .findFirst()
                .ifPresent(abo -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/cardAbonnement.fxml"));
                        HBox card = loader.load();

                        cardAbonnement controller = loader.getController();
                        controller.setUserData(abo);
                        controller.setListAbonnemant(this);

                        AbonnementContainer.getChildren().add(card); // Add new card to the container
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }



    void loadUsers() {
        if (SessionManager.getUtilisateur().getRole()== Utilisateur.Role.Client){

            Node topNode = ala.getTop();
            ToolBar toolBar = (ToolBar) topNode;

            toolBar.getItems().remove(crreatebuton);


        }
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
