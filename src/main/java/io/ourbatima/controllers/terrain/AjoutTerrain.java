package io.ourbatima.controllers.terrain;

import io.ourbatima.controllers.projet.AjoutProjet;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Terrain;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.concurrent.Worker;

import java.math.BigDecimal;

public class AjoutTerrain extends ActionView {

    @FXML
    private TextField emplacementField;

    @FXML
    private TextField caracteristiquesField;

    @FXML
    private TextField superficieField;

    @FXML
    private TextField detailsGeoField;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button openMapButton;

    private final TerrainDAO terrainDAO = new TerrainDAO();
    private Stage mapStage;
    private AjoutProjet ajoutProjetController;

    @FXML
    private void initialize() {
        ajouterButton.setDisable(true);

        emplacementField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        caracteristiquesField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        detailsGeoField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        superficieField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());

        superficieField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!e.getCharacter().matches("[0-9.]") || (e.getCharacter().equals(".") && superficieField.getText().contains("."))) {
                e.consume();
            }
        });
        ajouterButton.setOnAction(event -> handleAddTerrain());
        openMapButton.setOnAction(event -> handleMapClick());

    }

    @FXML
    private void handleMapClick() {
        System.out.println("📍 Ouverture de la carte...");
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", new JSBridge(this));

                requestLocationPermission(webEngine);

                System.out.println("✅ Bridge JS-Java initialisé");
            }
        });


        webEngine.load(getClass().getResource("/MapApi/mapApi.html").toExternalForm());

        mapStage = new Stage();
        mapStage.setScene(new Scene(webView));
        mapStage.setTitle("Sélection de position");
        mapStage.show();
    }
    public String getSuperficie() {
        return superficieField.getText().trim();
    }

    public void receiveCoordinates(double latitude, double longitude) {
        Platform.runLater(() -> {
            String geoDetails = "Latitude: " + latitude + ", Longitude: " + longitude;
            detailsGeoField.setText(geoDetails);
        });
    }

    private void requestLocationPermission(WebEngine webEngine) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Autorisation de localisation");
        alert.setHeaderText("L'application souhaite accéder à votre position.");
        alert.setContentText("Autoriser la géolocalisation ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("🌍 Autorisation accordée, récupération de la position...");
                webEngine.executeScript("locateUser();"); // Exécute la fonction JS
            } else {
                System.out.println("🚫 Autorisation refusée !");
            }
        });
    }

    public class JSBridge {
        private final AjoutTerrain handler;

        public JSBridge(AjoutTerrain handler) {
            this.handler = handler;
        }

        public void receiveLocation(double latitude, double longitude) {
            System.out.println("📌 Position reçue : Latitude = " + latitude + ", Longitude = " + longitude);
            handler.updateLocation(latitude, longitude);
        }

        public void sendAddress(String address, double latitude, double longitude) {
            System.out.println("📨 Adresse reçue depuis JS : " + address);
            handler.updateAddressField(address);
            String coordinates = "Latitude: " + latitude + ", Longitude: " + longitude;
            handler.updateLocationField(coordinates);
        }
    }

    public void updateLocationField(String coordinates) {
        Platform.runLater(() -> {
            if (detailsGeoField != null) {
                detailsGeoField.setText(coordinates);
                System.out.println("✅ Détails géographiques mis à jour : " + detailsGeoField.getText());
            } else {
                System.out.println("❌ ERREUR : detailsGeoField est NULL lors de la mise à jour !");
            }
        });
    }

    public void updateLocation(double latitude, double longitude) {
        Platform.runLater(() -> {
            System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
        });
    }

    public void updateAddressField(String address) {
        System.out.println("📨 Adresse reçue : " + address);

        Platform.runLater(() -> {
            if (emplacementField != null) {
                emplacementField.setText(address);
                System.out.println("✅ Adresse mise à jour : " + emplacementField.getText());
            } else {
                System.out.println("❌ ERREUR : emplacementField est NULL lors de la mise à jour !");
            }
            if (mapStage != null) mapStage.close();
        });
    }

    private void validateInput() {
        boolean isValid = !emplacementField.getText().trim().isEmpty() &&
                !caracteristiquesField.getText().trim().isEmpty() &&
                !detailsGeoField.getText().trim().isEmpty() &&
                isSuperficieValid();

        ajouterButton.setDisable(!isValid);
    }

    private boolean isSuperficieValid() {
        String superficieText = superficieField.getText().trim();
        if (superficieText.isEmpty()) {
            return false;
        }
        try {
            BigDecimal superficie = new BigDecimal(superficieText);
            return superficie.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setAjoutProjetController(AjoutProjet controller) {
        this.ajoutProjetController = controller;
    }


    @FXML
    private void handleAddTerrain() {
        System.out.println("Add Terrain button clicked!");
        String emplacement = emplacementField.getText().trim();
        String caracteristiques = caracteristiquesField.getText().trim();
        String superficieText = superficieField.getText().trim();
        String detailsGeo = detailsGeoField.getText().trim();

        if (emplacement.isEmpty() || caracteristiques.isEmpty() || detailsGeo.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires !");
            return;
        }

        BigDecimal superficie = null;
        if (!superficieText.isEmpty()) {
            try {
                superficie = new BigDecimal(superficieText);
            } catch (NumberFormatException e) {
                showAlert("Erreur", "La superficie doit être un nombre positif!");
                return;
            }
        }

        if (!showConfirmation("Confirmation", "Êtes-vous sûr de vouloir ajouter ce terrain ?")) {
            return;
        }

        Terrain terrain = new Terrain(0, detailsGeo, caracteristiques, superficie, emplacement);
        try {
            terrainDAO.addTerrain(terrain);
            showAlert("Succès", "Terrain ajouté avec succès !");
            if (ajoutProjetController != null) {
                ajoutProjetController.setTerrainTextField(emplacement);
            }
            clearFields();
            ((Stage) ajouterButton.getScene().getWindow()).close();

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du terrain : " + e.getMessage());
        }
    }

    private void clearFields() {
        emplacementField.clear();
        caracteristiquesField.clear();
        superficieField.clear();
        detailsGeoField.clear();
        validateInput();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}
