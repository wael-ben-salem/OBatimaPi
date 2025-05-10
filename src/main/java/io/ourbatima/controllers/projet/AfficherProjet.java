package io.ourbatima.controllers.projet;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Initializable;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



public class AfficherProjet extends ActionView implements Initializable {

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

    @Override
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

    private Optional<String> getEmailClientById(int id_client, Connection connection) throws SQLException {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateur = utilisateurDAO.getUserById(id_client, connection);

        if (utilisateur == null) {
            System.out.println("No user found for ID: " + id_client);
            return Optional.empty();
        }

        if (utilisateur.getRole() == Utilisateur.Role.Client) {
            String email = utilisateur.getEmail();
            if (email != null && !email.isEmpty()) {
                System.out.println("Client email found: " + email);
                return Optional.of(email);
            } else {
                System.out.println("Client has no email assigned.");
                return Optional.empty();
            }
        } else {
            System.out.println("User is not a client: " + utilisateur.getEmail());
            return Optional.empty();
        }
    }

    private void setupTable() {
        colProjet.setCellValueFactory(new PropertyValueFactory<>("nomProjet"));

        colClient.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            int idClient = projet.getId_client();
            System.out.println("Fetching email for client ID: " + idClient);

            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ourbatimapi", "root", "");
                Optional<String> email = getEmailClientById(idClient, connection);
                if (email.isPresent()) {
                    System.out.println("Client email found: " + email.get());
                    return new SimpleStringProperty(email.get());
                } else {
                    System.out.println("No client found for ID: " + idClient);
                    return new SimpleStringProperty("Aucun client attribué.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Database connection error");
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
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
                    etapes.stream().map(EtapeProjet::getNomEtape).collect(Collectors.joining("\n"))
                    : "No Steps";
            return new SimpleStringProperty(etapesString);
        });

        colActions.setCellFactory(new Callback<TableColumn<Projet, String>, TableCell<Projet, String>>() {
            @Override
            public TableCell<Projet, String> call(TableColumn<Projet, String> param) {
                return new TableCell<Projet, String>() {
                    private final Button updateButton = new Button();
                    private final Button deleteButton = new Button();
                    {
                        ImageView pencilIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/pencil.png")));
                        pencilIcon.setFitWidth(16);
                        pencilIcon.setFitHeight(16);

                        // Set the image as the button's graphic
                        updateButton.setGraphic(pencilIcon);
                        updateButton.setStyle("-fx-background-color: transparent;");

                        ImageView binIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/bin.png")));
                        binIcon.setFitWidth(20);
                        binIcon.setFitHeight(20);
                        deleteButton.setGraphic(binIcon);
                        deleteButton.setStyle("-fx-background-color: transparent;");                    }

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

                            HBox hBox = new HBox(5, updateButton, deleteButton);
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
        // Create a confirmation dialog
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation de suppression");
        confirmationDialog.setHeaderText("Êtes-vous sûr de vouloir supprimer ce projet ?");
        confirmationDialog.setContentText("Cette action est irréversible.");

        // Translate the buttons to French
        ButtonType confirmButton = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationDialog.getButtonTypes().setAll(confirmButton, cancelButton);

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        // If the user confirms, proceed with the deletion
        if (result.isPresent() && result.get() == confirmButton) {
            projetDAO.deleteProjet(projetId);
            projetData.removeIf(p -> p.getId_projet() == projetId);
            projetTable.setItems(projetData);

            // Show a success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Le projet a été supprimé avec succès.");
            successAlert.showAndWait();
        } else {
            // Show a cancellation message
            Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
            cancelAlert.setTitle("Annulation");
            cancelAlert.setHeaderText(null);
            cancelAlert.setContentText("La suppression du projet a été annulée.");
            cancelAlert.showAndWait();
        }
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        loadProjets();
        initialize();
    }
}