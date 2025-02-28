package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class EquipeDetailController extends ActionView {
    @FXML private Label nomLabel;
    @FXML private Label constructeurLabel;
    @FXML private Label gestionnaireLabel;
    @FXML private Label dateCreationLabel;
    @FXML private ListView<String> artisansListView;
    @FXML private VBox detailsContainer;

    private Equipe equipe;

    public void initData(Equipe equipe) {
        this.equipe = equipe;
        loadDetails();
    }

    private void loadDetails() {
        if (equipe != null) {
            nomLabel.setText(equipe.getNom());
            constructeurLabel.setText("Constructeur : " + equipe.getConstructeur().getNom() + " " + equipe.getConstructeur().getPrenom());
            gestionnaireLabel.setText("Gestionnaire : " + equipe.getGestionnaireStock().getNom() + " " + equipe.getGestionnaireStock().getPrenom());
            dateCreationLabel.setText("Date de création : " + equipe.getDateCreation().toString());

            // Charger la liste des artisans
            artisansListView.getItems().clear();
            equipe.getArtisans().forEach(artisan -> {
                artisansListView.getItems().add(artisan.getNom() + " " + artisan.getPrenom());
            });
        } else {
            detailsContainer.setVisible(false);
        }
    }
}