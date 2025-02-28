package io.ourbatima.controllers.terrain;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Visite.VisiteDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Refreshable;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Visite;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.stage.Stage;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherTerrain extends ActionView implements Refreshable {

    private final TerrainDAO terrainDAO = new TerrainDAO();
    private final VisiteDAO visiteDAO = new VisiteDAO();
     // Instance of the MapHandler

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private VBox vbox;
    @FXML
    private ListView<String> listViewEmplacement;

    @FXML
    private TextArea terrainDetails;

    @FXML
    public void initialize() {
        System.out.println("✅ AfficherTerrain Controller Initialized");
        System.out.println("🔍 listNomEtapes: " + listViewEmplacement);
        System.out.println("📝 etapeProjetDetails: " + terrainDetails);
        loadEmplacementList();
        setupClickListener();
    }

    @FXML
    private void loadEmplacementList() {
        List<Terrain> terrains = terrainDAO.getAllTerrain();

        if (terrains.isEmpty()) {
            showAlert("Aucun emplacement", "Aucun terrain n'a été trouvé dans la base de données.");
        } else {
            List<String> emplacementNames = terrains.stream()
                    .map(Terrain::getEmplacement)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                listViewEmplacement.getItems().clear();
                listViewEmplacement.getItems().setAll(emplacementNames);
                if (!terrains.isEmpty()) {
                    showTerrainDetails(terrains.get(0));
                }
            });
        }
    }


    private void setupClickListener() {
        listViewEmplacement.setOnMouseClicked(event -> {
            String selectedEmplacement = listViewEmplacement.getSelectionModel().getSelectedItem();
            System.out.println("📌 Clicked on: " + selectedEmplacement);

            if (selectedEmplacement != null) {
                Terrain terrain = terrainDAO.getTerrainByEmplacement(selectedEmplacement);
                System.out.println("🔎 Retrieved Terrain: " + terrain);

                if (terrain == null) {
                    System.out.println("⚠️ No matching Etape found in database!");
                } else {
                    System.out.println("Found Terrain: " + terrain.getEmplacement());
                    showTerrainDetails(terrain);
                }
            } else {
                System.out.println("❌ No item selected!");
                terrainDetails.setText("");
            }
        });
    }

    private void showTerrainDetails(Terrain terrain) {
        if (terrain == null) {
            terrainDetails.setText("Aucun détail trouvé pour cet emplacement.");
            return;
        }

        StringBuilder visitsDetails = new StringBuilder();
        List<String> observations = terrainDAO.getObservationsForTerrain(terrain.getId_terrain());
        if (observations == null || observations.isEmpty()) {
            visitsDetails.append("Aucune visite enregistrée");
        } else {
            for (String observation : observations) {
                visitsDetails.append("📝 Observations: ").append(observation).append("\n\n");
            }
        }

        String details = "📍    Emplacement: " + (terrain.getEmplacement() != null ? terrain.getEmplacement() : "N/A") + "\n" + "\n" +
                "🏗️  Caractéristiques: " + (terrain.getCaracteristiques() != null ? terrain.getCaracteristiques() : "N/A") + "\n" + "\n" +
                "📏    Superficie: " + (terrain.getSuperficie() != null ? terrain.getSuperficie() : "N/A") + "\n" + "\n" +
                "🌍    Détails Géographiques: " + (terrain.getDetailsGeo() != null ? terrain.getDetailsGeo() : "N/A") + "\n" + "\n" +
                "📅    " + visitsDetails.toString();

        terrainDetails.setText(details);
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        Platform.runLater(() -> {
            listViewEmplacement.getItems().clear();
            loadEmplacementList();
            initialize();
        });
    }

    @FXML
    private void handleUpdate() {
        String selectedEmplacement = listViewEmplacement.getSelectionModel().getSelectedItem();
        if (selectedEmplacement != null) {
            Terrain terrain = terrainDAO.getTerrainByEmplacement(selectedEmplacement);
            if (terrain != null) {
                // Create a dialog to update all details
                Dialog<Terrain> dialog = new Dialog<>();
                dialog.setTitle("Update Terrain");
                dialog.setHeaderText("Modify the details of the selected terrain.");

                // Create a VBox to hold the input fields
                VBox vbox = new VBox();
                TextField emplacementField = new TextField(terrain.getEmplacement());
                TextField caracteristiquesField = new TextField(terrain.getCaracteristiques());
                TextField superficieField = new TextField(terrain.getSuperficie() != null ? terrain.getSuperficie().toString() : "");
                TextField detailsGeoField = new TextField(terrain.getDetailsGeo());
                TextField observationsField = new TextField();

                // Populate observations field with existing observations
                observationsField.setText(terrain.getObservations() != null ? String.join(", ", terrain.getObservations()) : "");

                vbox.getChildren().addAll(
                        new Label("Emplacement:"), emplacementField,
                        new Label("Caractéristiques:"), caracteristiquesField,
                        new Label("Superficie:"), superficieField,
                        new Label("Détails Géographiques:"), detailsGeoField,
                        new Label("Observations:"), observationsField // For observations from Visite
                );

                dialog.getDialogPane().setContent(vbox);

                // Add buttons for dialog
                ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

                // Show the dialog and wait for the result
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == updateButtonType) {
                        // Create a new Terrain object with updated details
                        Terrain updatedTerrain = terrain;
                        updatedTerrain.setEmplacement(emplacementField.getText());
                        updatedTerrain.setCaracteristiques(caracteristiquesField.getText());
                        updatedTerrain.setSuperficie(superficieField.getText().isEmpty() ? null : new BigDecimal(superficieField.getText()));
                        updatedTerrain.setDetailsGeo(detailsGeoField.getText());
                        updatedTerrain.setObservations(List.of(observationsField.getText().split(",")));
                        return updatedTerrain;
                    }
                    return null;
                });

                Optional<Terrain> result = dialog.showAndWait();
                result.ifPresent(updatedTerrain -> {
                    terrainDAO.updateTerrain(updatedTerrain); // Update the terrain details
                    loadEmplacementList(); // Refresh the list

                    // Refresh the details after updating
                    Terrain refreshedTerrain = terrainDAO.getTerrainByEmplacement(updatedTerrain.getEmplacement());
                    showTerrainDetails(refreshedTerrain); // Show updated terrain details with new observations
                });
            } else {
                showAlert("Update Error", "Terrain not found for updating.");
            }
        } else {
            showAlert("Selection Error", "No terrain selected for updating.");
        }
    }




    @Override
    public void refresh() {
        String selectedEmplacement = listViewEmplacement.getSelectionModel().getSelectedItem();
        if (selectedEmplacement != null) {
            Terrain terrain = terrainDAO.getTerrainByEmplacement(selectedEmplacement);
            if (terrain != null) {
                showTerrainDetails(terrain);
            }
        }
    }

    @FXML
    private void handleDelete() {
        String selectedEmplacement = listViewEmplacement.getSelectionModel().getSelectedItem();
        if (selectedEmplacement != null) {
            Terrain terrain = terrainDAO.getTerrainByEmplacement(selectedEmplacement);
            if (terrain != null) {
                terrainDAO.deleteTerrain(terrain.getId_terrain()); // Call the delete method with the ID
                loadEmplacementList(); // Refresh the list after deletion
                showAlert("Success", "Terrain deleted successfully.");
            } else {
                showAlert("Deletion Error", "Terrain not found for deletion.");
            }
        } else {
            showAlert("Selection Error", "No terrain selected for deletion.");
        }
    }






}
