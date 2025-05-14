package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.financeModel.Contrat;
import io.ourbatima.core.model.financeModel.ContratDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class ListContrats extends ActionView {

    @FXML
    private TableView<ContratDTO> tableContrats;

    @FXML
    private TableColumn<ContratDTO, Void> actionsColumn;

    @FXML private TextField searchField;

    @FXML private ListView<String> suggestionsProjetList;

    private final ContratServise cs = new ContratServise();

    @FXML
    public void initialize() {
        System.out.println("Initializing ListContrats...");
        if (tableContrats == null) {
            System.err.println("Error: tableContrats is null!");
            return;
        }

        // Set the cell factory for the Actions column
        actionsColumn.setCellFactory(createButtonCellFactory());
        loadAndDisplayContrats();
    }

    private void filterContractsByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            suggestionsProjetList.getItems().clear();
            loadAndDisplayContrats();
            return;
        }

        // Get the list of all contracts
        List<ContratDTO> allContrats = cs.getAllContart().stream()
                .map(contrat -> {
                    try {
                        int idProjet = contrat.getIdProjet();
                        String nomClient = cs.getClientNOMEtidbyidcontrat(idProjet);
                        String nomProjet = getNOMFromDatabase(idProjet);

                        return new ContratDTO(
                                contrat.getIdContrat(),
                                contrat.getTypeContrat(),
                                contrat.getDateSignature(),
                                contrat.getDateDebut(),
                                contrat.isSignatureElectronique(),
                                contrat.getDateFin(),
                                contrat.getMontantTotal(),
                                contrat.getIdProjet(),
                                nomClient,
                                nomProjet
                        );

                    } catch (SQLException e) {
                        System.err.println("Error fetching contract details: " + e.getMessage());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Filter the contracts based on project name (nomProjet)
        ObservableList<String> filteredSuggestions = FXCollections.observableArrayList(
                allContrats.stream()
                        .filter(contrat -> contrat.getNomProjet().toLowerCase().contains(query.toLowerCase()))
                        .map(ContratDTO::getNomProjet)
                        .distinct()
                        .collect(Collectors.toList())
        );


        // Set the filtered list of suggestions in the ListView
        Platform.runLater(() -> {
            suggestionsProjetList.setItems(filteredSuggestions);
        });    }

    private void onSuggestionSelected(String selectedProjet) {
        if (selectedProjet != null) {
            List<ContratDTO> matchingContracts = cs.getAllContart().stream()
                    .map(contrat -> {
                        try {
                            int idProjet = contrat.getIdProjet();
                            String nomClient = cs.getClientNOMEtidbyidcontrat(idProjet);
                            String nomProjet = getNOMFromDatabase(idProjet);

                            return new ContratDTO(
                                    contrat.getIdContrat(),
                                    contrat.getTypeContrat(),
                                    contrat.getDateSignature(),
                                    contrat.getDateDebut(),
                                    contrat.isSignatureElectronique(),
                                    contrat.getDateFin(),
                                    contrat.getMontantTotal(),
                                    contrat.getIdProjet(),
                                    nomClient,
                                    nomProjet
                            );

                        } catch (SQLException e) {
                            System.err.println("Error fetching contract details: " + e.getMessage());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .filter(contrat -> contrat.getNomProjet().equalsIgnoreCase(selectedProjet))
                    .collect(Collectors.toList());

            // Update the table with matching contracts
            ObservableList<ContratDTO> contractsToShow = FXCollections.observableArrayList(matchingContracts);
            tableContrats.setItems(contractsToShow);
        }
    }



    private Callback<TableColumn<ContratDTO, Void>, TableCell<ContratDTO, Void>> createButtonCellFactory() {
        return param -> new TableCell<ContratDTO, Void>() {
            private final HBox container = new HBox(5);
            private final Button updateButton = new Button();
            private final Button deleteButton = new Button();
            private final Button consulterButton = new Button();

            {
                // Update button with icon
                ImageView updateIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/pencil.png")));
                updateIcon.setFitWidth(16);
                updateIcon.setFitHeight(16);
                updateButton.setGraphic(updateIcon);
                updateButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
                updateButton.getStyleClass().add("update-button");

                // Delete button with icon
                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/bin.png")));
                deleteIcon.setFitWidth(16);
                deleteIcon.setFitHeight(16);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
                deleteButton.getStyleClass().add("delete-button");

                // Consulter button with icon
                ImageView consulterIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/eopen.png")));
                consulterIcon.setFitWidth(16);
                consulterIcon.setFitHeight(16);
                consulterButton.setGraphic(consulterIcon);
                consulterButton.setStyle("-fx-background-color: transparent; -fx-padding: 2;");

                // Set button actions (keep your existing functionality)
                updateButton.setOnAction(event -> {
                    ContratDTO rowData = getTableView().getItems().get(getIndex());
                    handleUpdateAction(rowData);
                });

                deleteButton.setOnAction(event -> {
                    ContratDTO rowData = getTableView().getItems().get(getIndex());
                    handleDeleteAction(rowData);
                });

                consulterButton.setOnAction(event -> {
                    ContratDTO rowData = getTableView().getItems().get(getIndex());
                    try {
                        hndleaConsulter(rowData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                container.getChildren().addAll(updateButton, deleteButton, consulterButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        };

    }

    private void hndleaConsulter(ContratDTO rowData) throws IOException {
        Contrat con =new Contrat(rowData.getIdContrat(),rowData.getTypeContrat(),rowData.getDateSignature(),rowData.getDateDebut(),rowData.isSignatureElectronique(),rowData.getDateFin(),rowData.getMontantTotal(),rowData.getIdProjet());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/Finance_vews/ConsulterContratAjouter.fxml"));
        Parent root = loader.load();

        // Get the controller instance created by the FXMLLoader
        ConsulterContratAjouter controller = loader.getController();

        // Set the Contrat object and load data
        controller.setdataCOntrat(con);

        // Show the new stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

    }

    private void loadAndDisplayContrats() {
        try {
            ObservableList<ContratDTO> contrats = FXCollections.observableArrayList(loadContrats());
            tableContrats.setItems(contrats);
        } catch (Exception e) {
            System.err.println("Error loading contracts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/Finance_vews/AjouterContrat.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter Contrat");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // Refresh after adding
            loadAndDisplayContrats();
        } catch (IOException e) {
            System.err.println("Error opening AjouterContrat.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<ContratDTO> loadContrats() {
        return cs.getAllContart().stream()
                .map(contrat -> {
                    try {
                        int idProjet = contrat.getIdProjet();
                        String nomClient = cs.getClientNOMEtidbyidcontrat(idProjet);
                        String nomProjet = getNOMFromDatabase(idProjet);

                        // Return the new DTO
                        return new ContratDTO(
                                contrat.getIdContrat(),             // int idContrat
                                contrat.getTypeContrat(),            // String typeContrat
                                contrat.getDateSignature(),          // Date dateSignature
                                contrat.getDateDebut(),              // Date dateDebut
                                contrat.isSignatureElectronique(),  // String signatureElectronique
                                contrat.getDateFin(),                // Date dateFin
                                contrat.getMontantTotal(),           // double montantTotal
                                contrat.getIdProjet(),               // int idProjet
                                nomClient,                           // String nomClient (retrieved from your method)
                                nomProjet                            // String nomProjet (retrieved from your method)
                        );

                    } catch (SQLException e) {
                        System.err.println("Error fetching contract details: " + e.getMessage());
                    }
                    return null; // Return null in case of exception
                })
                .filter(Objects::nonNull) // Remove null values if any
                .collect(Collectors.toList());
    }

    public String getNOMFromDatabase(int Id_projet) {
        String name = "";
        try (Connection conn = getConnection()) {
            String query = "SELECT nomProjet FROM projet WHERE Id_projet = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Id_projet);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                name = rs.getString("nomProjet");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return name;
    }

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        loadAndDisplayContrats();
        actionsColumn.setCellFactory(createButtonCellFactory());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterContractsByName(newValue);
        });
        suggestionsProjetList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                onSuggestionSelected(newValue);
            }
        });
        suggestionsProjetList.setVisible(true);
        suggestionsProjetList.setManaged(true);

    }



    private void handleUpdateAction(ContratDTO rowData) {
        // Handle update action
        try {
            // Load the FXML file for the update popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/UpdateContrat.fxml"));
            Parent root = loader.load();

            // Get the controller for the popup
            UpdateContratController updateController = loader.getController();

            // Pass the selected ContratDTO object to the popup controller
            updateController.setContratData(rowData);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            popupStage.setTitle("Update Contract");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); // Wait for the popup to close

            // Refresh the table after the popup is closed
            loadAndDisplayContrats();
        } catch (IOException e) {
            System.err.println("Error opening UpdateContrat.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleDeleteAction(ContratDTO rowData) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer Confirmation");
        alert.setContentText("vouler vous supprimer ce contrat?");

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // If the user clicks "OK" (or "Yes"), delete the data
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Call the service to delete the contract
                cs.delitecontrat(rowData.getIdContrat()); // Assuming `deleteContrat` is a method in your service

                // Refresh the table after deletion
                loadAndDisplayContrats();

                System.out.println("Contract deleted: " + rowData);
            } catch (Exception e) {
                System.err.println("Error deleting contract: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }
}