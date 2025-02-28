package io.ourbatima.controllers.projet;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Utilisateur.Client;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



public class AfficherProjet extends ActionView {

    private final ProjetDAO projetDAO = new ProjetDAO();
    @FXML private TableView<Projet> projetTable;
    @FXML private TableColumn<Projet, String> colProjet;
    @FXML private TableColumn<Projet, String> colClient;
    @FXML private TableColumn<Projet, String> colEquipe;
    @FXML private TableColumn<Projet, BigDecimal> colBudget;
    @FXML private TableColumn<Projet, String> colType;
    @FXML private TableColumn<Projet, String> colStyleArch;
    @FXML private TableColumn<Projet, String> colEmplacement;
    @FXML private TableColumn<Projet, String> colEtapes;
    @FXML private TableColumn<Projet, String> colEtat;
    @FXML private TableColumn<Projet, Timestamp> colDateCreation;
    @FXML private TableColumn<Projet, String> colActions;
    @FXML private TextField searchField;
    @FXML private ListView<String> suggestionsList;


    private ObservableList<Projet> projetData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("AfficherProjet Controller Initialized");
        setupTable();
        loadProjets();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchInput();
            if (newValue.isEmpty()) {
                suggestionsList.setVisible(false);
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
                searchField.setText(selectedSuggestion);
                suggestionsList.setVisible(false);
                handleSearch();
            }
        });

    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<Projet> filteredList = projetData.stream()
                .filter(projet -> projet.getNomProjet().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        projetTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    private void handleSearchInput() {
        String searchText = searchField.getText().toLowerCase();
        List<String> suggestions = projetData.stream()
                .map(Projet::getNomProjet)
                .filter(nom -> nom.toLowerCase().startsWith(searchText))
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            suggestionsList.getItems().setAll(suggestions);
            suggestionsList.setVisible(true);
        } else {
            suggestionsList.setVisible(false);
        }
    }

    private Optional<String> getEmailClientById(int id_client) {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateur = utilisateurDAO.getUserById(id_client);
        if (utilisateur == null) {
            return Optional.empty();
        }

        if (utilisateur instanceof Client) {
            return Optional.ofNullable(utilisateur.getEmail());
        } else {
            return Optional.empty();
        }
    }

    private void setupTable() {
        colProjet.setCellValueFactory(new PropertyValueFactory<>("nomProjet"));

        colClient.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            int idClient = projet.getId_client();
            System.out.println("Fetching email for client ID: " + idClient);
            Optional<String> email = getEmailClientById(idClient);
            if (email.isPresent()) {
                System.out.println("Client email found: " + email.get());
                return new SimpleStringProperty(email.get());
            } else {
                System.out.println("No client found for ID: " + idClient);
                return new SimpleStringProperty("Aucun client attribué.");
            }
        });


        colEquipe.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleStringProperty(projet.getNomEquipe());
        });

        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStyleArch.setCellValueFactory(new PropertyValueFactory<>("styleArch"));

        colEmplacement.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleStringProperty(projet.getEmplacement());
        });

        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        colEtapes.setCellValueFactory(cellData -> {
            List<EtapeProjet> etapes = cellData.getValue().getEtapes();
            String etapesString = (etapes != null) ?
                    etapes.stream().map(EtapeProjet::getNomEtape).collect(Collectors.joining(", "))
                    : "No Steps";
            return new SimpleStringProperty(etapesString);
        });

        colActions.setCellFactory(new Callback<TableColumn<Projet, String>, TableCell<Projet, String>>() {
            @Override
            public TableCell<Projet, String> call(TableColumn<Projet, String> param) {
                return new TableCell<Projet, String>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    {
                        updateButton.setStyle("-fx-background-color: #b39427; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #4c3f0a; -fx-text-fill: white;");
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {

                            updateButton.setOnAction(event -> {
                                Projet projet = getTableView().getItems().get(getIndex());
                                openUpdateProjetWindow(projet);
                            });

                            deleteButton.setOnAction(event -> {
                                Projet projet = getTableView().getItems().get(getIndex());
                                deleteProjet(projet.getId_projet());
                            });

                            HBox hBox = new HBox(10, updateButton, deleteButton);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }
                };
            }
        });


    }

    private void openUpdateProjetWindow(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Projet/UpdateProjet.fxml"));
            Parent root = loader.load();
            UpdateProjet controller = loader.getController();
            controller.initData(projet, this);

            Stage stage = new Stage();
            stage.setTitle("Update Project");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre de modification.");
            alert.showAndWait();
        }
    }

    @FXML
    public void loadProjets() {
        try {
            List<Projet> projets = projetDAO.getAllProjets();
            System.out.println("Fetched Projects: " + projets);
            for (Projet projet : projets) {
                System.out.println("Project ID: " + projet.getId_projet() + ", Client ID: " + projet.getId_client());
            }
            projetData.setAll(projets);
            projetTable.setItems(null);
            projetTable.layout();
            projetTable.setItems(projetData);
            projetTable.refresh();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Échec du chargement des projets : " + e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }


    private void deleteProjet(int projetId) {
        projetDAO.deleteProjet(projetId);
        projetData.removeIf(p -> p.getId_projet() == projetId);
        projetTable.setItems(projetData);
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        loadProjets();
        initialize();
    }





}