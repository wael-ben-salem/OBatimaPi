package io.ourbatima.controllers.terrain;

import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Terrain;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ButtonType;
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

    private final TerrainDAO terrainDAO = new TerrainDAO();

    @FXML
    private void initialize() {
        ajouterButton.setOnAction(event -> ajouterTerrain());
        ajouterButton.setDisable(true);
        addInputListeners();
    }

    private void addInputListeners() {
        emplacementField.addEventFilter(KeyEvent.KEY_RELEASED, e -> validateInput());
        caracteristiquesField.addEventFilter(KeyEvent.KEY_RELEASED, e -> validateInput());
        superficieField.addEventFilter(KeyEvent.KEY_RELEASED, e -> validateInput());
        detailsGeoField.addEventFilter(KeyEvent.KEY_RELEASED, e -> validateInput());
    }

    private void validateInput() {
        boolean isValid = !emplacementField.getText().trim().isEmpty() &&
                !caracteristiquesField.getText().trim().isEmpty() &&
                isSuperficieValid() &&
                !detailsGeoField.getText().trim().isEmpty();
        ajouterButton.setDisable(!isValid);
    }

    private boolean isSuperficieValid() {
        String superficieText = superficieField.getText().trim();
        if (superficieText.isEmpty()) {
            return true;
        }
        try {
            BigDecimal superficie = new BigDecimal(superficieText);
            return superficie.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void ajouterTerrain() {
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

        superficieField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!e.getCharacter().matches("[0-9]") && !e.getCharacter().equals(".")) {
                e.consume();
            }
        });

        if (!showConfirmation("Confirmation", "Êtes-vous sûr de vouloir ajouter ce terrain ?")) {
            return;
        }

        Terrain terrain = new Terrain(0, null, detailsGeo, superficie, caracteristiques, emplacement, null);
        try {
            terrainDAO.addTerrain(terrain);
            showAlert("Succès", "Terrain ajouté avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout du terrain : " + e.getMessage());
        }
        clearFields();
    }

    private void clearFields() {
        emplacementField.clear();
        caracteristiquesField.clear();
        superficieField.clear();
        detailsGeoField.clear();
        ajouterButton.setDisable(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

}
