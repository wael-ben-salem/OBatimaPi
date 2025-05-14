package io.ourbatima.controllers.projet;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Initializable;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Utilisateur.Client;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class AfficherProjet extends ActionView implements Initializable {

    private final ProjetDAO projetDAO = new ProjetDAO();
    @FXML
    private TableView<Projet> projetTable;
    @FXML
    private TableColumn<Projet, String> colProjet;
    @FXML
    private TableColumn<Projet, String> colClient;
    @FXML
    private TableColumn<Projet, String> colEquipe;
    @FXML
    private TableColumn<Projet, BigDecimal> colBudget;
    @FXML
    private TableColumn<Projet, String> colType;
    @FXML
    private TableColumn<Projet, String> colStyleArch;
    @FXML
    private TableColumn<Projet, String> colEmplacement;
    @FXML
    private TableColumn<Projet, String> colEtapes;
    @FXML
    private TableColumn<Projet, String> colEtat;
    @FXML
    private TableColumn<Projet, Timestamp> colDateCreation;
    @FXML
    private TableColumn<Projet, String> colActions;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> suggestionsList;
    @FXML
    private Button AjoutProjet;

    private ObservableList<Projet> projetData = FXCollections.observableArrayList();

    @Override
    public void initialize() {
        System.out.println("AfficherProjet Controller Initialized");
        setupTable();
        loadProjets();
        AjoutProjet.setOnAction(event -> handleAjoutProjet());

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

        // Improved client email retrieval with better error handling and logging
        colClient.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            int idClient = projet.getId_client();
            System.out.println("[DEBUG] Fetching email for client ID: " + idClient);

            try (Connection conn = DatabaseConnection.getConnection()) {
                // First verify the client exists
                String clientCheckSql = "SELECT 1 FROM client WHERE client_id = ?";
                try (PreparedStatement clientCheckStmt = conn.prepareStatement(clientCheckSql)) {
                    clientCheckStmt.setInt(1, idClient);
                    try (ResultSet rs = clientCheckStmt.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("[WARNING] No client found with ID: " + idClient);
                            return new SimpleStringProperty("Client non trouvé");
                        }
                    }
                }

                // Then get the email
                String emailSql = "SELECT email FROM utilisateur WHERE id = ?";
                try (PreparedStatement emailStmt = conn.prepareStatement(emailSql)) {
                    emailStmt.setInt(1, idClient);
                    try (ResultSet rs = emailStmt.executeQuery()) {
                        if (rs.next()) {
                            String email = rs.getString("email");
                            System.out.println("[DEBUG] Found email for client " + idClient + ": " + email);
                            return new SimpleStringProperty(email);
                        } else {
                            System.out.println("[WARNING] No email found for client ID: " + idClient);
                            return new SimpleStringProperty("Email non trouvé");
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("[ERROR] Database error fetching client email: " + e.getMessage());
                return new SimpleStringProperty("Erreur base de données");
            }
        });

        // Team name column
        colEquipe.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            String teamName = (projet.getNomEquipe() != null) ? projet.getNomEquipe() : "Aucune équipe";
            return new SimpleStringProperty(teamName);
        });

        // Budget column (formatted as currency)
        colBudget.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleObjectProperty<>(projet.getBudget());
        });
        colBudget.setCellFactory(tc -> new TableCell<Projet, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f TND", item));
                }
            }
        });

        // Project type column
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Architectural style column
        colStyleArch.setCellValueFactory(new PropertyValueFactory<>("styleArch"));

        // Location column
        colEmplacement.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            String location = (projet.getEmplacement() != null) ? projet.getEmplacement() : "Non spécifié";
            return new SimpleStringProperty(location);
        });

        // Status column
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Creation date column (formatted)
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colDateCreation.setCellFactory(tc -> new TableCell<Projet, Timestamp>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toLocalDateTime().format(formatter));
                }
            }
        });

        // Project steps column
        colEtapes.setCellValueFactory(cellData -> {
            List<EtapeProjet> etapes = cellData.getValue().getEtapes();
            String etapesString = (etapes != null && !etapes.isEmpty()) ?
                    etapes.stream()
                            .map(EtapeProjet::getNomEtape)
                            .collect(Collectors.joining(", "))
                    : "Aucune étape";
            return new SimpleStringProperty(etapesString);
        });

        // Actions column (Edit/Delete buttons)
        colActions.setCellFactory(new Callback<TableColumn<Projet, String>, TableCell<Projet, String>>() {
            @Override
            public TableCell<Projet, String> call(TableColumn<Projet, String> param) {
                return new TableCell<Projet, String>() {
                    private final Button updateButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox buttonBox = new HBox(5, updateButton, deleteButton);

                    {
                        // Edit button setup
                        ImageView pencilIcon = new ImageView(
                                new Image(getClass().getResourceAsStream("/images/pencil.png")));
                        pencilIcon.setFitWidth(16);
                        pencilIcon.setFitHeight(16);
                        updateButton.setGraphic(pencilIcon);
                        updateButton.setStyle("-fx-background-color: transparent;");
                        updateButton.setTooltip(new Tooltip("Modifier le projet"));

                        // Delete button setup
                        ImageView binIcon = new ImageView(
                                new Image(getClass().getResourceAsStream("/images/bin.png")));
                        binIcon.setFitWidth(16);
                        binIcon.setFitHeight(16);
                        deleteButton.setGraphic(binIcon);
                        deleteButton.setStyle("-fx-background-color: transparent;");
                        deleteButton.setTooltip(new Tooltip("Supprimer le projet"));

                        // Button actions
                        updateButton.setOnAction(event -> {
                            Projet projet = getTableView().getItems().get(getIndex());
                            openUpdateProjetWindow(projet);
                        });

                        deleteButton.setOnAction(event -> {
                            Projet projet = getTableView().getItems().get(getIndex());
                            deleteProjet(projet.getId_projet());
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttonBox);
                        }
                    }
                };
            }
        });

        // Add context menu for the table
        ContextMenu contextMenu = new ContextMenu();
        MenuItem refreshItem = new MenuItem("Actualiser");
        refreshItem.setOnAction(e -> handleReload());
        contextMenu.getItems().add(refreshItem);
        projetTable.setContextMenu(contextMenu);
    }

    @FXML
    private void handleAjoutProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Projet/ajoutProjet.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un nouveau projet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the table after the new project window closes
            loadProjets();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre d'ajout de projet.");
            alert.showAndWait();
        }
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