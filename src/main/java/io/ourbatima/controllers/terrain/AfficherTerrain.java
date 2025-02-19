package io.ourbatima.controllers.terrain;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Visite.VisiteDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Visite;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.List;

public class AfficherTerrain extends ActionView {

    private final TerrainDAO terrainDAO = new TerrainDAO();
    private final VisiteDAO visiteDAO = new VisiteDAO();
    private final ProjetDAO projetDAO = new ProjetDAO();


    @FXML
    private ListView<String> listViewEmplacement;

    @FXML
    private TextArea terrainDetails;

    @FXML
    public void initialize() {
        loadEmplacementList();
        setupClickListener();
    }


    private void loadEmplacementList() {
        List<Terrain> terrains = terrainDAO.getAllTerrain();
        for (Terrain terrain : terrains) {
            if (terrain.getEmplacement() != null) {
                listViewEmplacement.getItems().add(terrain.getEmplacement());
            }
        }
    }


    private void setupClickListener() {
        listViewEmplacement.setOnMouseClicked(event -> {
            String selectedEmplacement = listViewEmplacement.getSelectionModel().getSelectedItem();
            if (selectedEmplacement != null) {
                Terrain terrain = terrainDAO.getTerrainByEmplacement(selectedEmplacement);
                showTerrainDetails(terrain);
            }
        });
    }


    private void showTerrainDetails(Terrain terrain) {
        String dateVisite = "Aucune visite enregistrée";

        if (terrain.getId_visite() != null) {
            Visite visite = visiteDAO.getVisiteById(terrain.getId_visite());
            if (visite != null) {
                dateVisite = visite.getDateVisite().toString();
            }
        }

        String nomProjet = "Aucun projet associé";
        if (terrain.getId_projet() != null) {
            Projet projet = projetDAO.getProjetById(terrain.getId_projet());
            if (projet != null) {
                nomProjet = projet.getNomProjet();
            }
        }

        String details = "📍 Emplacement: " + terrain.getEmplacement() + "\n" +
                "🏗️ Caractéristiques: " + terrain.getCaracteristiques() + "\n" +
                "📏 Superficie: " + terrain.getSuperficie() + "\n" +
                "🌍 Détails Géographiques: " + terrain.getDetailsGeo() + "\n" +
                "🏢 Projet Associé: " + nomProjet + "\n" +
                "📅 Date de Visite: " + dateVisite;

        terrainDetails.setText(details);
    }
}
