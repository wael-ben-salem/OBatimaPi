package io.ourbatima.controllers.terrain;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Visite.VisiteDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Initializable;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherTerrain extends ActionView implements Refreshable, Initializable {

    private final TerrainDAO terrainDAO = new TerrainDAO();
    private final VisiteDAO visiteDAO = new VisiteDAO();

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private VBox vbox;
    @FXML private ListView<String> listViewEmplacement;
    @FXML private TextArea terrainDetails;
    @FXML private WebView mapView;


    public AfficherTerrain() {}

    @Override
    public void initialize() {
        System.out.println("✅ AfficherTerrain Controller Initialized");
        System.out.println("🔍 listNomEtapes: " + listViewEmplacement);
        System.out.println("📝 etapeProjetDetails: " + terrainDetails);

        // Set up custom cell factory for listViewEmplacement
        listViewEmplacement.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 12px; -fx-font-weight: bold;");
                }
            }
        });

        setupClickListener();
        Platform.runLater(() -> {
            listViewEmplacement.getItems().clear();
            loadEmplacementList();
        });
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        initialize();
    }

    @FXML
    private void loadEmplacementList() {
        List<Terrain> terrains = terrainDAO.getAllTerrain();

        if (terrains.isEmpty()) {
            showAlert("Aucun emplacement", "Aucun terrain n'a été trouvé dans la base de données.");
        } else {
            System.out.println("✅ Found " + terrains.size() + " terrains.");
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
        System.out.println("🔍 showTerrainDetails called for terrain: " + terrain);

        if (terrain == null) {
            System.out.println("⚠️ Terrain is null!");
            terrainDetails.setText("Aucun détail trouvé pour cet emplacement.");
            return;
        }

        // Extract coordinates
        double[] coordinates = extractCoordinates(terrain.getDetailsGeo());
        if (coordinates != null) {
            // Generate map URL
            String mapUrl = generateMapImageUrl(coordinates[0], coordinates[1]);
            System.out.println("🌍 Map URL: " + mapUrl);

            // Load map in WebView
            WebEngine webEngine = mapView.getEngine();
            webEngine.load(mapUrl);
        } else {
            System.out.println("⚠️ No valid coordinates found for terrain.");
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

        System.out.println("🔍 Terrain details: " + details);
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
                    terrainDAO.updateTerrain(updatedTerrain);
                    loadEmplacementList();

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
                terrainDAO.deleteTerrain(terrain.getId_terrain());
                loadEmplacementList(); //
                showAlert("Success", "Terrain deleted successfully.");
            } else {
                showAlert("Deletion Error", "Terrain not found for deletion.");
            }
        } else {
            showAlert("Selection Error", "No terrain selected for deletion.");
        }
    }

    private double[] extractCoordinates(String detailsGeo) {
        if (detailsGeo == null || !detailsGeo.contains("Latitude:") || !detailsGeo.contains("Longitude:")) {
            return null;
        }

        try {
            String[] parts = detailsGeo.split(",");
            double latitude = Double.parseDouble(parts[0].replace("Latitude:", "").trim());
            double longitude = Double.parseDouble(parts[1].replace("Longitude:", "").trim());
            return new double[]{latitude, longitude};
        } catch (Exception e) {
            System.err.println("Failed to extract coordinates: " + e.getMessage());
            return null;
        }
    }

//    //Google Maps Static API
//    private String generateMapImageUrl(double latitude, double longitude) {
//        String apiKey = "YOUR_GOOGLE_MAPS_API_KEY"; // Replace with your Google Maps API key
//        String baseUrl = "https://maps.googleapis.com/maps/api/staticmap";
//        String center = latitude + "," + longitude;
//        String zoom = "15"; // Zoom level
//        String size = "400x200"; // Image size
//        String marker = "color:red|label:T|" + latitude + "," + longitude;
//
//        return baseUrl + "?center=" + center + "&zoom=" + zoom + "&size=" + size + "&markers=" + marker + "&key=" + apiKey;
//    }

 //   OpenStreetMap
    private String generateMapImageUrl(double latitude, double longitude) {
        String baseUrl = "https://www.openstreetmap.org/export/embed.html";
        String bbox = (longitude - 0.01) + "," + (latitude - 0.01) + "," + (longitude + 0.01) + "," + (latitude + 0.01);
        return baseUrl + "?bbox=" + bbox + "&layer=mapnik";
    }



}
