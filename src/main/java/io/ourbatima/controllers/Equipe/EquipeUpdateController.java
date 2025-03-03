package io.ourbatima.controllers.Equipe;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EquipeUpdateController extends ActionView {
    private static final Logger logger = Logger.getLogger(EquipeUpdateController.class.getName());

    @FXML private TextField nomField;
    @FXML private ComboBox<Constructeur> constructeurCombo;
    @FXML private ComboBox<GestionnaireDeStock> gestionnaireCombo;
    @FXML private DatePicker dateCreationPicker;
    @FXML private HBox constructeurContainer;
    @FXML private HBox gestionnaireContainer;
    @FXML private ListView<Artisan> assignedList;
    @FXML private ListView<Artisan> availableList;


    @FXML private FlowPane constructeursFlow;
    @FXML private FlowPane gestionnairesFlow;

    @FXML private ImageView selectedConstructeurImage;
    @FXML private Label selectedConstructeurLabel;
    @FXML private ImageView selectedGestionnaireImage;
    @FXML private Label selectedGestionnaireLabel;

    private final Map<Integer, Image> constructeurImageCache = new HashMap<>();
    private final Map<Integer, Image> gestionnaireImageCache = new HashMap<>();

    private Equipe equipe;
    private Runnable refreshCallback;

    // ===================== INITIALISATION =====================
    public void initData(Equipe equipe, List<Constructeur> constructeurs, List<GestionnaireDeStock> gestionnaires) throws SQLException {
        this.equipe = equipe;
        // Préchargement des images
        preloadImages(constructeurs, gestionnaires);
        constructeurCombo.setItems(FXCollections.observableArrayList(constructeurs));
        gestionnaireCombo.setItems(FXCollections.observableArrayList(gestionnaires));
        loadData();
        initArtisansData(equipe); // Chargement des artisans

    }

    private void preloadImages(List<Constructeur> constructeurs, List<GestionnaireDeStock> gestionnaires) {
        // Constructeurs
        for (Constructeur c : constructeurs) {
            Image img = loadImage(c.getConstructeur_id(), "constructeurs", "con");
            constructeurImageCache.put(c.getConstructeur_id(), img);
        }

        // Gestionnaires
        for (GestionnaireDeStock g : gestionnaires) {
            Image img = loadImage(g.getGestionnairestock_id(), "gestionnaires", "ges");
            gestionnaireImageCache.put(g.getGestionnairestock_id(), img);
        }
    }

    public void initArtisansData(Equipe equipe) throws SQLException {
        EquipeDAO equipeDAO = new EquipeDAO();
        List<Artisan> assigned = equipeDAO.getAssignedArtisans(equipe.getId());
        List<Artisan> available = equipeDAO.findAvailableArtisans(equipe.getId());

        assignedList.getItems().setAll(assigned);
        availableList.getItems().setAll(available);

        setupDragDrop();
        setupCellFactories();
    }
    private void setupCellFactories() {
        assignedList.setCellFactory(param -> new ArtisanCell());
        availableList.setCellFactory(param -> new ArtisanCell());
    }

    private void setupDragDrop() {
        // Configuration du drag & drop entre les deux listes
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
                    source.getItems().remove(artisan);
                    target.getItems().add(artisan);

                    // Mise à jour visuelle immédiate
                    source.refresh();
                    target.refresh();
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

    // Classe interne pour l'affichage
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

        private Image loadArtisanImage(Artisan artisan) {
            String path = String.format("/images/artisans/art%d.png", artisan.getArtisan_id());
            try {
                return new Image(getClass().getResourceAsStream(path));
            } catch (Exception e) {
                return new Image(getClass().getResourceAsStream("/images/artisans/default.png"));
            }
        }
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
        // Chargement plus robuste de l'image par défaut
        return loadDefaultImage(folder);
    }

    private Image loadDefaultImage(String folder) {
        String path = String.format("/images/%s/default.png", folder);
        try (InputStream is = getClass().getResourceAsStream(path)) {
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    private void initialize() {
        setupComboboxes();
        setupImageListeners();
        setupSelectionListeners();
    }

    private void setupSelectionListeners() {
        // Listener pour le constructeur
        constructeurCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSelectedConstructeur(newVal);
            }
        });

        // Listener pour le gestionnaire
        gestionnaireCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSelectedGestionnaire(newVal);
            }
        });
    }

    private void updateSelectedConstructeur(Constructeur constructeur) {
        Image image = constructeurImageCache.getOrDefault(
                constructeur.getConstructeur_id(),
                loadDefaultImage("constructeurs")
        );
        selectedConstructeurImage.setImage(image);
        selectedConstructeurLabel.setText(constructeur.getNom());

        // Mise à jour de la carte si visible

    }

    private void updateSelectedGestionnaire(GestionnaireDeStock gestionnaire) {
        Image image = gestionnaireImageCache.getOrDefault(
                gestionnaire.getGestionnairestock_id(),
                loadDefaultImage("gestionnaires")
        );
        selectedGestionnaireImage.setImage(image);
        selectedGestionnaireLabel.setText(gestionnaire.getNom());

        // Mise à jour de la carte si visible

    }

    @FXML
    private void toggleTeamVisibility() {
        try {
            logger.info("Bouton 'Voir les équipes' cliqué");



            refreshTeamCard();

        } catch (Exception e) {
            logger.severe("Erreur lors de l'affichage de la carte des équipes : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'affichage des équipes.", Alert.AlertType.ERROR);
        }
    }
    private void refreshTeamCard() {
        try {
            logger.info("Rafraîchissement de la carte des équipes");

            // Mise à jour du constructeur
            Constructeur currentConstructeur = constructeurCombo.getValue();
            if (currentConstructeur != null) {
                logger.info("Mise à jour du constructeur sélectionné : " + currentConstructeur.getNom());
                updateSelectedConstructeur(currentConstructeur);
            }

            // Mise à jour du gestionnaire
            GestionnaireDeStock currentGestionnaire = gestionnaireCombo.getValue();
            if (currentGestionnaire != null) {
                logger.info("Mise à jour du gestionnaire sélectionné : " + currentGestionnaire.getNom());
                updateSelectedGestionnaire(currentGestionnaire);
            }
        } catch (Exception e) {
            logger.severe("Erreur lors du rafraîchissement de la carte des équipes : " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors du rafraîchissement de la carte.", Alert.AlertType.ERROR);
        }
    }

    // ===================== GESTION DES IMAGES =====================
    private void setupImageListeners() {
        constructeurCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            refreshConstructeurImage();
        });

        gestionnaireCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            refreshGestionnaireImage();
        });
    }

    private void refreshConstructeurImage() {
        Constructeur selected = constructeurCombo.getValue();
        if (selected != null) {
            Image img = constructeurImageCache.getOrDefault(
                    selected.getConstructeur_id(),
                    loadDefaultImage("constructeurs")
            );
            applyImageDirect(selectedConstructeurImage, img);
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

    private void applyImageDirect(ImageView imageView, Image image) {
        imageView.setImage(image);
        imageView.setOpacity(1.0); // Réinitialiser l'opacité
    }

    private void applyImageWithEffect(ImageView imageView, Image image) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(50), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), imageView);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            imageView.setImage(image);
            fadeIn.play();
            forceImageUpdate(imageView); // Forçage supplémentaire
        });

        fadeOut.play();
    }

    private void forceImageUpdate(ImageView imageView) {
        Image temp = imageView.getImage();
        imageView.setImage(null);
        imageView.setImage(temp);
    }

    private void updateImage(int id, String folder, String prefix, ImageView imageView) {
        imageView.setImage(null); // Réinitialisation

        String[] extensions = {"png", "jpg", "jpeg"};
        for (String ext : extensions) {
            String imagePath = String.format("/images/%s/%s%d.%s", folder, prefix, id, ext);

            try (InputStream is = getClass().getResourceAsStream(imagePath)) {
                if (is != null) {
                    Image image = new Image(is);
                    imageView.setImage(image);
                    System.out.println("Image chargée : " + imagePath);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Échec de chargement : " + imagePath);
            }
        }

        // Fallback
        String defaultPath = String.format("/images/%s/default.png", folder);
        try (InputStream is = getClass().getResourceAsStream(defaultPath)) {
            if (is != null) {
                imageView.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Image par défaut manquante");
        }
    }

    // ===================== CONFIGURATION DES COMBOBOXES =====================
    private void setupComboboxes() {
        // Configuration pour Constructeur
        setupCombo(constructeurCombo);
        setupCombo(gestionnaireCombo);
    }

    private <T> void setupCombo(ComboBox<T> combo) {
        combo.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(item.toString());
            }
        });

        combo.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(item.toString());
            }
        });
    }

    // ===================== CHARGEMENT DES DONNÉES =====================
    private void loadData() {
        if (equipe != null) {
            nomField.setText(equipe.getNom());
            dateCreationPicker.setValue(equipe.getDateCreation());

            // Chargement initial synchrone
            selectCurrentValues();
            if (constructeurCombo.getValue() != null) {
                updateSelectedConstructeur(constructeurCombo.getValue());
            }
            if (gestionnaireCombo.getValue() != null) {
                updateSelectedGestionnaire(gestionnaireCombo.getValue());
            }
        }
    }

    private void selectCurrentValues() {
        // Sélection du constructeur actuel
        selectInCombo(constructeurCombo, equipe.getConstructeur().getConstructeur_id());
        // Sélection du gestionnaire actuel
        selectInCombo(gestionnaireCombo, equipe.getGestionnaireStock().getGestionnairestock_id());
    }

    private <T> void selectInCombo(ComboBox<T> combo, int id) {
        combo.getItems().stream()
                .filter(item -> {
                    if (item instanceof Constructeur)
                        return ((Constructeur) item).getConstructeur_id() == id;
                    else if (item instanceof GestionnaireDeStock)
                        return ((GestionnaireDeStock) item).getGestionnairestock_id() == id;
                    return false;
                })
                .findFirst()
                .ifPresent(item -> {
                    T selected = combo.getSelectionModel().getSelectedItem();

                    // Modification clé : vérification null-safe
                    if (selected == null || !selected.equals(item)) {
                        combo.getSelectionModel().select(item);
                    }

                    // Forçage du rafraîchissement dans tous les cas
                    Platform.runLater(() -> {
                        if (combo == constructeurCombo) {
                            refreshConstructeurImage();
                        } else if (combo == gestionnaireCombo) {
                            refreshGestionnaireImage();
                        }
                    });
                });
    }

    private void forceRefresh() {
        selectedConstructeurImage.setImage(null);
        selectedGestionnaireImage.setImage(null);

        // Ajout d'un délai contrôlé
        new Thread(() -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                refreshConstructeurImage();
                refreshGestionnaireImage();
            });
        }).start();
    }

    // ===================== GESTION DU FORMULAIRE =====================
    @FXML
    private void save() {
        if (validateForm()) {
            try {
                // Vérification des champs modifiés et mise à jour des valeurs uniquement pour ceux qui ont changé
                if (!nomField.getText().equals(equipe.getNom())) {
                    equipe.setNom(nomField.getText());
                }
                if (!dateCreationPicker.getValue().equals(equipe.getDateCreation())) {
                    equipe.setDateCreation(dateCreationPicker.getValue());
                }
                if (!constructeurCombo.getValue().equals(equipe.getConstructeur())) {
                    equipe.setConstructeur(constructeurCombo.getValue());
                }
                if (!gestionnaireCombo.getValue().equals(equipe.getGestionnaireStock())) {
                    equipe.setGestionnaireStock(gestionnaireCombo.getValue());
                }
                equipe.setArtisans(assignedList.getItems());

                // Mise à jour de l'équipe dans la base de données
                new EquipeDAO().update(equipe);

                // Si un callback de rafraîchissement est défini, on l'exécute
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                showAlert("Mise a jour de l'equipe:", "La mise a jour a effectuer avec succée : " , Alert.AlertType.CONFIRMATION);

                closeWindow();
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Échec de la mise à jour : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    private void updateEquipeFromForm() {
        equipe.setNom(nomField.getText());
        equipe.setDateCreation(dateCreationPicker.getValue());
        equipe.setConstructeur(constructeurCombo.getValue());
        equipe.setGestionnaireStock(gestionnaireCombo.getValue());
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().isBlank()) errors.append("- Nom de l'équipe requis\n");
        if (constructeurCombo.getValue() == null) errors.append("- Constructeur non sélectionné\n");
        if (gestionnaireCombo.getValue() == null) errors.append("- Gestionnaire non sélectionné\n");
        if (assignedList.getItems().isEmpty()) {
            errors.append("- La liste des assignés ne doit pas être vide\n");
        }

        if (!errors.isEmpty()) {
            showAlert("Validation", "Corrigez les erreurs :\n" + errors, Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void closeWindow() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        new Alert(type, message).showAndWait();
    }

    @FXML
    private void close() {
        closeWindow();
    }

    private void reinitImageView(ImageView iv, HBox container) {
        if (container.getChildren().contains(iv)) {
            int index = container.getChildren().indexOf(iv);
            ImageView newIv = createNewImageView(iv);
            container.getChildren().set(index, newIv);

            // Mise à jour de la référence dans le contrôleur
            if (container == constructeurContainer) {
                selectedConstructeurImage = newIv;
            } else if (container == gestionnaireContainer) {
                selectedGestionnaireImage = newIv;
            }
        } else {
            ImageView newIv = createNewImageView(iv);
            container.getChildren().add(newIv);
        }
    }

    private void populateTeamCard() {
        constructeursFlow.getChildren().clear();
        gestionnairesFlow.getChildren().clear();

        // Ajout des constructeurs
        constructeurCombo.getItems().forEach(c -> {
            ImageView iv = createTeamImageView(
                    constructeurImageCache.get(c.getConstructeur_id()),
                    c.getNom()
            );
            constructeursFlow.getChildren().add(iv);
        });

        // Ajout des gestionnaires
        gestionnaireCombo.getItems().forEach(g -> {
            ImageView iv = createTeamImageView(
                    gestionnaireImageCache.get(g.getGestionnairestock_id()),
                    g.getNom()
            );
            gestionnairesFlow.getChildren().add(iv);
        });
    }

    private ImageView createTeamImageView(Image image, String tooltipText) {
        ImageView iv = new ImageView(image);
        iv.setFitWidth(60);
        iv.setFitHeight(60);
        iv.setPreserveRatio(true);
        iv.getStyleClass().add("team-member-image");

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(iv, tooltip);

        return iv;
    }

    private ImageView createNewImageView(ImageView original) {
        ImageView newIv = new ImageView();
        newIv.setFitWidth(40);
        newIv.setFitHeight(40);
        newIv.setEffect(original.getEffect());
        newIv.getStyleClass().addAll(original.getStyleClass());
        return newIv;
    }
}