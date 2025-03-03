package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.ArtisanDAO;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Equipe;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsertArtisanController extends ActionView {

    @FXML private ListView<Artisan> assignedList;
    @FXML private ListView<Artisan> availableList;
    @FXML private Label teamNameLabel;
    @FXML private Label constructeurLabel;
    @FXML private Label gestionnaireLabel;
    @FXML private ImageView constructeurImage;
    @FXML private ImageView gestionnaireImage;

    private Equipe equipe;
    private Runnable refreshCallback;

    public void initData(Equipe equipe) {
        this.equipe = equipe;
        loadTeamInfo();
        loadArtisans();
        setupDragDrop();
    }


    private Image loadImage(int id, String folder, String prefix) {
        String[] extensions = {"png", "jpg", "jpeg"};
        for (String ext : extensions) {
            String path = String.format("/images/%s/%s%d.%s", folder, prefix, id, ext);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) return new Image(is);
            } catch (Exception e) {
                System.out.println("Erreur chargement: " + path);
            }
        }
        return loadDefaultImage(folder);
    }
    private Image loadDefaultImage(String folder) {
        String path = String.format("/images/%s/default.png", folder);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new Image(is);
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/images/default-avatar.png"));
        }
    }

    private void loadArtisans() {
        try {
            EquipeDAO equipeDAO = new EquipeDAO();

            List<Artisan> assignedArtisans = equipeDAO.getAssignedArtisans(equipe.getId());
            List<Artisan> availableArtisans = equipeDAO.findAvailableArtisans(equipe.getId());

            assignedList.getItems().setAll(assignedArtisans);
            availableList.getItems().setAll(availableArtisans);

            setupCellFactories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupCellFactories() {
        assignedList.setCellFactory(param -> new ArtisanCell());
        availableList.setCellFactory(param -> new ArtisanCell());
    }

    private void setupDragDrop() {
        // Drag from available to assigned
        availableList.setOnDragDetected(event -> {
            Artisan selected = availableList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Dragboard db = availableList.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(selected.getArtisan_id()));
                db.setContent(content);
                event.consume();
            }
        });

        assignedList.setOnDragOver(event -> {
            if (event.getGestureSource() != assignedList &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        assignedList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int artisanId = Integer.parseInt(db.getString());
                Artisan artisan = findArtisanById(artisanId, availableList.getItems());

                if (artisan != null) {
                    availableList.getItems().remove(artisan);
                    assignedList.getItems().add(artisan);
                }

                event.setDropCompleted(true);
            }
            event.consume();
        });

        // Drag from assigned to available (même logique inverse)
        // ... Implémentez la logique inverse similaire
    }

    private Artisan findArtisanById(int id, List<Artisan> artisans) {
        return artisans.stream()
                .filter(a -> a.getArtisan_id() == id)
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void save() {
        try {
            List<Artisan> updatedArtisans = new ArrayList<>(assignedList.getItems());
            EquipeDAO equipeDAO = new EquipeDAO();
            equipeDAO.updateArtisans(equipe.getId(), updatedArtisans);

            if (refreshCallback != null) {
                refreshCallback.run();
            }
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void close() {
        ((Stage) teamNameLabel.getScene().getWindow()).close();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    // Classe interne pour l'affichage des artisans
    private class ArtisanCell extends ListCell<Artisan> {
        @Override
        protected void updateItem(Artisan artisan, boolean empty) {
            super.updateItem(artisan, empty);
            if (empty || artisan == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox(10);

                // Chargement d'image comme pour les constructeurs
                ImageView imageView = new ImageView(
                        loadImage(artisan.getArtisan_id(), "artisans", "art")
                );
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);

                Label nameLabel = new Label(artisan.getNom());
                Label specialiteLabel = new Label(artisan.getSpecialite().toString());

                hbox.getChildren().addAll(imageView, new VBox(nameLabel, specialiteLabel));
                setGraphic(hbox);
            }
        }
    }

    private void loadTeamInfo() {
        teamNameLabel.setText(equipe.getNom());
        constructeurLabel.setText(equipe.getConstructeur().getNom());
        gestionnaireLabel.setText(equipe.getGestionnaireStock().getNom());

        // Chargement des images avec la nouvelle méthode
        constructeurImage.setImage(loadImage(
                equipe.getConstructeur().getConstructeur_id(),
                "constructeurs",
                "con"
        ));

        gestionnaireImage.setImage(loadImage(
                equipe.getGestionnaireStock().getGestionnairestock_id(),
                "gestionnaires",
                "ges"
        ));
    }
}