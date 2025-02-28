package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.financeModel.ContratDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

        // Load and display contracts
        loadAndDisplayContrats();
    }

    private Callback<TableColumn<ContratDTO, Void>, TableCell<ContratDTO, Void>> createButtonCellFactory() {
        return param -> new TableCell<ContratDTO, Void>() {
            private final HBox container = new HBox(5); // HBox to hold buttons
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                // Set button styles
                updateButton.getStyleClass().add("update-button");
                deleteButton.getStyleClass().add("delete-button");

                // Set button actions
                updateButton.setOnAction(event -> {
                    ContratDTO rowData = getTableView().getItems().get(getIndex());
                    handleUpdateAction(rowData);
                });

                deleteButton.setOnAction(event -> {
                    ContratDTO rowData = getTableView().getItems().get(getIndex());
                    handleDeleteAction(rowData);
                });

                container.getChildren().addAll(updateButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Hide buttons for empty rows
                } else {
                    setGraphic(container); // Show buttons for non-empty rows
                }
            }
        };
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

    public void alolao(ActionEvent event) {
        System.out.println("Initializing ListContrats...");
        if (tableContrats == null) {
            System.err.println("Error: tableContrats is null!");
            return;
        }
        loadAndDisplayContrats();
        actionsColumn.setCellFactory(createButtonCellFactory());

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