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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML private TextField searchField;
    @FXML private ListView<String> suggestionsTerrainList;
    @FXML private Button Search;



    public AfficherTerrain() {}

    @Override
    public void initialize() {
        System.out.println("✅ AfficherTerrain Controller Initialized");
        System.out.println("🔍 listNomEtapes: " + listViewEmplacement);
        System.out.println("📝 etapeProjetDetails: " + terrainDetails);
        Search.setOnAction(event -> handleSearch());

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
        setupSearchField();
    }
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadEmplacementList(); // Load all if search is empty
        } else {
            List<Terrain> filteredTerrains = terrainDAO.getAllTerrain().stream()
                    .filter(terrain -> terrain.getEmplacement().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            updateListView(filteredTerrains);
            if (!filteredTerrains.isEmpty()) {
                showTerrainDetails(filteredTerrains.get(0));
            }
        }
    }
    private void updateListView(List<Terrain> terrains) {
        List<String> emplacementNames = terrains.stream()
                .map(Terrain::getEmplacement)
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            listViewEmplacement.getItems().clear();
            listViewEmplacement.getItems().setAll(emplacementNames);
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                suggestionsTerrainList.setVisible(false); // Hide suggestions if input is empty
                suggestionsTerrainList.getItems().clear();
                return;
            }

            List<Terrain> terrains = terrainDAO.getAllTerrain();
            List<String> matchingEmplacements = terrains.stream()
                    .map(Terrain::getEmplacement)
                    .filter(emplacement -> emplacement.toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());

            if (matchingEmplacements.isEmpty()) {
                suggestionsTerrainList.setVisible(false); // Hide suggestions if no matches found
            } else {
                suggestionsTerrainList.getItems().setAll(matchingEmplacements);
                suggestionsTerrainList.setVisible(true); // Show suggestions if matches found
            }
        });

        // Handle selection from suggestions
        suggestionsTerrainList.setOnMouseClicked(event -> {
            String selected = suggestionsTerrainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                searchField.setText(selected);
                suggestionsTerrainList.setVisible(false);
                selectTerrainByEmplacement(selected);
            }
        });
    }


    private void selectTerrainByEmplacement(String emplacement) {
        Terrain terrain = terrainDAO.getTerrainByEmplacement(emplacement);
        if (terrain != null) {
            showTerrainDetails(terrain);
            listViewEmplacement.getSelectionModel().select(emplacement);
        }
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
                try {
                    // Load the UpdateTerrain.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/terrain/UpdateTerrain.fxml"));
                    Parent root = loader.load();

                    UpdateTerrain controller = loader.getController();
                    controller.initData(terrain);

                    Stage stage = new Stage();
                    stage.setTitle("Update Terrain");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    loadEmplacementList();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load the update window.");
                }
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
                // Show confirmation dialog
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmer la suppression");
                confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce terrain ?");
                confirmationAlert.setContentText("Cette action est irréversible.");

                // Wait for user response
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Proceed with deletion if confirmed
                    terrainDAO.deleteTerrain(terrain.getId_terrain());
                    loadEmplacementList(); // Refresh the list
                    showAlert("Succès", "Terrain supprimé avec succès.");
                } else {
                    // If user cancels, show cancellation message (optional)
                    showAlert("Suppression annulée", "Aucun terrain n'a été supprimé.");
                }
            } else {
                showAlert("Erreur de suppression", "Le terrain n'a pas été trouvé pour la suppression.");
            }
        } else {
            showAlert("Erreur de sélection", "Aucun terrain sélectionné pour la suppression.");
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