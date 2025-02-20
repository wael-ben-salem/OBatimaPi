package io.ourbatima.controllers.EtapeProjet;

import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Rapport.RapportDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Rapport;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherEtapeProjet extends ActionView {

    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();
    private final ProjetDAO projetDAO = new ProjetDAO();
    private final RapportDAO rapportDAO = new RapportDAO();



    @FXML
    private ListView<String> listNomEtapes;

    @FXML
    private TextArea etapeProjetDetails;

    @FXML
    public void initialize() {
        System.out.println("✅ AfficherEtapeProjet Controller Initialized");
        System.out.println("🔍 listNomEtapes: " + listNomEtapes);
        loadEtapeList();
        setupClickListener();
    }

    private void loadEtapeList() {
        List<EtapeProjet> etapes = etapeProjetDAO.getAllEtapeProjets();

        if (etapes.isEmpty()) {
            showAlert("Aucune étape", "Aucune étape de projet n'a été trouvée dans la base de données.");
        } else {
            List<String> etapeNames = etapes.stream()
                    .map(EtapeProjet::getNomEtape)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                listNomEtapes.getItems().clear();
                listNomEtapes.getItems().setAll(etapeNames);
            });
        }
    }

    private void setupClickListener() {
        listNomEtapes.setOnMouseClicked(event -> {
            String selectedEtape = listNomEtapes.getSelectionModel().getSelectedItem();
            System.out.println("📌 Clicked on: " + selectedEtape);

            if (selectedEtape != null) {
                EtapeProjet etape = etapeProjetDAO.getEtapeProjetByNom(selectedEtape);
                if (etape == null) {
                    System.out.println("⚠️ No matching Etape found in database!");
                } else {
                    System.out.println("✅ Found EtapeProjet: " + etape.getNomEtape());
                    showEtapeDetails(etape);
                }
            }
        });
    }

    private void showEtapeDetails(EtapeProjet etape) {
        if (etape == null) {
            System.out.println("⚠️ Cannot display details: etape is null!");
            etapeProjetDetails.setText("❌ Aucune donnée trouvée.");
            return;
        }

        System.out.println("📋 Displaying details for: " + etape.getNomEtape());

        String nomProjet = "Aucun projet associé";
        if (etape.getId_projet() > 0) {
            Projet projet = projetDAO.getProjetById(etape.getId_projet());
            if (projet != null) {
                nomProjet = projet.getNomProjet();
            } else {
                System.out.println("⚠️ No project found for ID: " + etape.getId_projet());
            }
        }

        String titre = "Aucun rapport associé";
        if (etape.getId_rapport() != null) {
            Rapport rapport = rapportDAO.getRapportById(etape.getId_rapport());
            if (rapport != null) {
                titre = rapport.getTitre();
            } else {
                System.out.println("⚠️ No report found for ID: " + etape.getId_rapport());
            }
        }

        String details = "📝 Nom Étape: " + etape.getNomEtape() + "\n" +
                "📖 Description: " + etape.getDescription() + "\n" +
                "📅 Date Début: " + etape.getDateDebut() + "\n" +
                "📅 Date Fin: " + etape.getDateFin() + "\n" +
                "📊 Statut: " + etape.getStatut() + "\n" +
                "💰 Montant: " + etape.getMontant() + "\n" +
                "🏢 Projet Associé: " + nomProjet + "\n" +
                "📄 Rapport: " + titre;

        etapeProjetDetails.setText(details);
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
        loadEtapeList(); // Reloads the ListView
    }


}
