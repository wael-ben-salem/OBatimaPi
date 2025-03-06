package io.ourbatima.controllers.terrain;

import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.model.Terrain;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class UpdateTerrain {

    @FXML
    private TextField caracteristiquesField;

    @FXML
    private TextField surperficieField;

    private Terrain terrain;
    private TerrainDAO terrainDAO = new TerrainDAO();

    public void initData(Terrain terrain) {
        this.terrain = terrain;
        caracteristiquesField.setText(terrain.getCaracteristiques());
        surperficieField.setText(terrain.getSuperficie() != null ? terrain.getSuperficie().toString() : "");
    }

    @FXML
    private void handleUpdate() {
        terrain.setCaracteristiques(caracteristiquesField.getText());
        terrain.setSuperficie(surperficieField.getText().isEmpty() ? null : new BigDecimal(surperficieField.getText()));
        terrainDAO.updateTerrain(terrain);

        Stage stage = (Stage) surperficieField.getScene().getWindow();
        stage.close();
    }
}