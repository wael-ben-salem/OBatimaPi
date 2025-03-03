package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import io.ourbatima.core.model.Utilisateur.Constructeur;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Utilisateur.GestionnaireDeStock;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EquipeCreateController extends ActionView {
    private static final Logger logger = Logger.getLogger(EquipeCreateController.class.getName());

    @FXML private TextField nomField;
    @FXML private ComboBox<Constructeur> constructeurCombo;
    @FXML private ComboBox<GestionnaireDeStock> gestionnaireCombo;
    @FXML private DatePicker dateCreationPicker;
    @FXML private ListView<Artisan> assignedList;
    @FXML private ListView<Artisan> availableList;
    @FXML private ImageView selectedConstructeurImage;
    @FXML private Label selectedConstructeurLabel;
    @FXML private ImageView selectedGestionnaireImage;
    @FXML private Label selectedGestionnaireLabel;

    private final Map<Integer, Image> constructeurImageCache = new HashMap<>();
    private final Map<Integer, Image> gestionnaireImageCache = new HashMap<>();
    private Runnable refreshCallback;

    @FXML
    private void initialize() {
        setupComboboxes();
        setupImageListeners();
        setupDatePicker();
    }

    private void setupComboboxes() {
        // Configuration des ComboBox pour les Constructeurs
        constructeurCombo.setButtonCell(new ListCell<Constructeur>() {
            @Override
            protected void updateItem(Constructeur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        constructeurCombo.setCellFactory(lv -> new ListCell<Constructeur>() {
            @Override
            protected void updateItem(Constructeur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        // Configuration des ComboBox pour les Gestionnaires
        gestionnaireCombo.setButtonCell(new ListCell<GestionnaireDeStock>() {
            @Override
            protected void updateItem(GestionnaireDeStock item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        gestionnaireCombo.setCellFactory(lv -> new ListCell<GestionnaireDeStock>() {
            @Override
            protected void updateItem(GestionnaireDeStock item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
    }

    private void setupImageListeners() {
        constructeurCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) refreshConstructeurImage();
        });

        gestionnaireCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) refreshGestionnaireImage();
        });
    }

    private void setupDatePicker() {
        dateCreationPicker.setValue(LocalDate.now());
        dateCreationPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });
    }

    public void initData(List<Constructeur> constructeurs,
                         List<GestionnaireDeStock> gestionnaires,
                         List<Artisan> artisansDisponibles) {

        preloadImages(constructeurs, gestionnaires);
        constructeurCombo.setItems(FXCollections.observableArrayList(constructeurs));
        gestionnaireCombo.setItems(FXCollections.observableArrayList(gestionnaires));
        availableList.setItems(FXCollections.observableArrayList(artisansDisponibles));

        setupDragDrop();
        setupCellFactories();
        setupSelectionListeners();
    }

    private void preloadImages(List<Constructeur> constructeurs, List<GestionnaireDeStock> gestionnaires) {
        for (Constructeur c : constructeurs) {
            Image img = loadImage(c.getConstructeur_id(), "constructeurs", "con");
            constructeurImageCache.put(c.getConstructeur_id(), img);
        }

        for (GestionnaireDeStock g : gestionnaires) {
            Image img = loadImage(g.getGestionnairestock_id(), "gestionnaires", "ges");
            gestionnaireImageCache.put(g.getGestionnairestock_id(), img);
        }
    }

    private void setupDragDrop() {
        setupListDragHandlers(availableList, assignedList);
        setupListDragHandlers(assignedList, availableList);
    }

    private void setupListDragHandlers(ListView<Artisan> source, ListView<Artisan> target) {
        source.setOnDragDetected(event -> {
            Artisan selected = source.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(selected.getArtisan_id()));
                db.setContent(content);
                event.consume();
            }
        });

        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        target.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int artisanId = Integer.parseInt(db.getString());
                Artisan artisan = findArtisanById(artisanId, source.getItems());

                if (artisan != null) {
                    Platform.runLater(() -> {
                        source.getItems().remove(artisan);
                        target.getItems().add(artisan);
                        source.refresh();
                        target.refresh();
                    });
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    private Artisan findArtisanById(int id, List<Artisan> artisans) {
        return artisans.stream()
                .filter(a -> a.getArtisan_id() == id)
                .findFirst()
                .orElse(null);
    }

    private void setupCellFactories() {
        assignedList.setCellFactory(param -> new ArtisanCell());
        availableList.setCellFactory(param -> new ArtisanCell());
    }

    private Image loadImage(int id, String folder, String prefix) {
        String[] extensions = {"png", "jpg", "jpeg"};
        for (String ext : extensions) {
            String path = String.format("/images/%s/%s%d.%s", folder, prefix, id, ext);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) return new Image(is);
            } catch (Exception e) {
                logger.warning("Erreur de chargement d'image: " + path);
            }
        }
        return loadDefaultImage(folder);
    }

    private Image loadDefaultImage(String folder) {
        String path = String.format("/images/%s/default.png", folder);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new Image(is);
        } catch (Exception e) {
            logger.severe("Image par défaut manquante pour: " + folder);
            return null;
        }
    }

    private void setupSelectionListeners() {
        constructeurCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateSelectedConstructeur(newVal);
        });

        gestionnaireCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateSelectedGestionnaire(newVal);
        });
    }

    private void updateSelectedConstructeur(Constructeur constructeur) {
        Image image = constructeurImageCache.getOrDefault(
                constructeur.getConstructeur_id(),
                loadDefaultImage("constructeurs")
        );
        applyImageWithEffect(selectedConstructeurImage, image);
        selectedConstructeurLabel.setText(constructeur.getNom());
    }

    private void updateSelectedGestionnaire(GestionnaireDeStock gestionnaire) {
        Image image = gestionnaireImageCache.getOrDefault(
                gestionnaire.getGestionnairestock_id(),
                loadDefaultImage("gestionnaires")
        );
        applyImageWithEffect(selectedGestionnaireImage, image);
        selectedGestionnaireLabel.setText(gestionnaire.getNom());
    }

    private void applyImageWithEffect(ImageView imageView, Image image) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), imageView);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            imageView.setImage(image);
            fadeIn.play();
        });
        fadeOut.play();
    }

    @FXML
    private void save() {
        if (validateForm()) {
            try {
                // Vérifier que le constructeur existe
                Constructeur constructeur = constructeurCombo.getValue();
                if (constructeur == null || new UtilisateurDAO().getConstructeurId(constructeur.getConstructeur_id()) == null) {
                    showAlert("Erreur", "Le constructeur sélectionné n'existe pas.", Alert.AlertType.ERROR);
                    return;
                }

                // Créer l'équipe
                Equipe nouvelleEquipe = new Equipe(
                        0,
                        nomField.getText(),
                        constructeur,
                        gestionnaireCombo.getValue(),
                        dateCreationPicker.getValue(),
                        FXCollections.observableArrayList(assignedList.getItems())
                );

                new EquipeDAO().create(nouvelleEquipe);

                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                showAlert("Succès", "Équipe créée avec succès !", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la création : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().isBlank()) errors.append("- Nom de l'équipe requis\n");
        if (dateCreationPicker.getValue() == null) errors.append("- Date de création requise\n");
        if (constructeurCombo.getValue() == null) errors.append("- Constructeur non sélectionné\n");
        if (gestionnaireCombo.getValue() == null) errors.append("- Gestionnaire non sélectionné\n");
        if (assignedList.getItems().isEmpty()) errors.append("- Au moins un artisan requis\n");

        if (!errors.isEmpty()) {
            showAlert("Validation", "Corrigez les erreurs :\n" + errors, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    private void close() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    private static class ArtisanCell extends ListCell<Artisan> {
        @Override
        protected void updateItem(Artisan artisan, boolean empty) {
            super.updateItem(artisan, empty);
            if (empty || artisan == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox(10);
                ImageView iv = new ImageView(loadArtisanImage(artisan));
                iv.setFitWidth(40);
                iv.setFitHeight(40);

                VBox labels = new VBox(
                        new Label(artisan.getNom()),
                        new Label(artisan.getSpecialite().toString())
                );

                hbox.getChildren().addAll(iv, labels);
                setGraphic(hbox);
            }
        }

        private static Image loadArtisanImage(Artisan artisan) {
            String path = String.format("/images/artisans/art%d.png", artisan.getArtisan_id());
            try {
                return new Image(EquipeCreateController.class.getResourceAsStream(path));
            } catch (Exception e) {
                return new Image(EquipeCreateController.class.getResourceAsStream("/images/artisans/default.png"));
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void refreshConstructeurImage() {
        Constructeur selected = constructeurCombo.getValue();
        if (selected != null) {
            Image img = constructeurImageCache.getOrDefault(
                    selected.getConstructeur_id(),
                    loadDefaultImage("constructeurs")
            );
            applyImageWithEffect(selectedConstructeurImage, img);
        }
    }

    private void refreshGestionnaireImage() {
        GestionnaireDeStock selected = gestionnaireCombo.getValue();
        if (selected != null) {
            Image img = gestionnaireImageCache.getOrDefault(
                    selected.getGestionnairestock_id(),
                    loadDefaultImage("gestionnaires")
            );
            applyImageWithEffect(selectedGestionnaireImage, img);
        }
    }

}