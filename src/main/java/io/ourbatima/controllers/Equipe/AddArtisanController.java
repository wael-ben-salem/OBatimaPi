package io.ourbatima.controllers.Equipe;
import io.ourbatima.core.Dao.Utilisateur.ArtisanDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Artisan;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddArtisanController extends ActionView {
    @FXML
    private TextField searchField;
    @FXML private ListView<Artisan> artisansList;
    @FXML private FlowPane selectedContainer;

    private List<Artisan> selectedArtisans = new ArrayList<>();
    private List<Artisan> availableArtisans;
    private boolean confirmed = false;

    public void setAvailableArtisans(List<Artisan> artisans) {
        this.availableArtisans = artisans;
        initializeList();
        setupSearchFilter();
    }

    private void initializeList() {
        FilteredList<Artisan> filteredData = new FilteredList<>(FXCollections.observableArrayList(availableArtisans));
        artisansList.setItems(filteredData);

        artisansList.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private final ImageView avatar = new ImageView();
            private final Label nameLabel = new Label();
            private final Label specialiteLabel = new Label();

            @Override
            protected void updateItem(Artisan artisan, boolean empty) {
                super.updateItem(artisan, empty);
                if (empty || artisan == null) {
                    setGraphic(null);
                } else {
                    configureCell(artisan);
                    setGraphic(buildCellContent());
                }
            }

            private void configureCell(Artisan artisan) {
                avatar.setImage(loadArtisanImage(artisan));
                nameLabel.setText(artisan.getNom());
                specialiteLabel.setText(artisan.getSpecialite().toString());
                checkBox.setSelected(selectedArtisans.contains(artisan));

                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if(newVal) {
                        selectedArtisans.add(artisan);
                    } else {
                        selectedArtisans.remove(artisan);
                    }
                    updateSelectedPreview();
                });
            }

            private HBox buildCellContent() {
                HBox hbox = new HBox(10, checkBox, avatar, new VBox(5, nameLabel, specialiteLabel));
                hbox.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5px;");
                return hbox;
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            FilteredList<Artisan> filtered = artisansList.getItems().filtered(a ->
                    a.getNom().toLowerCase().contains(newVal.toLowerCase()) ||
                            a.getSpecialite().toString().toLowerCase().contains(newVal.toLowerCase())
            );
            artisansList.setItems(filtered);
        });
    }

    private void updateSelectedPreview() {
        selectedContainer.getChildren().clear();
        selectedArtisans.forEach(artisan -> {
            ImageView iv = new ImageView(loadArtisanImage(artisan));
            iv.setFitWidth(40);
            iv.setFitHeight(40);
            iv.getStyleClass().add("selected-avatar");
            selectedContainer.getChildren().add(iv);
        });
    }

    private Image loadArtisanImage(Artisan artisan) {
        try {
            // Exemple de récupération d'image (à adapter)
            return new Image(getClass().getResourceAsStream(
                    "/images/equipes/art" + artisan.getArtisan_id() + ".png"
            ));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/ourbatima/style/avatars/man1@50.png"));
        }
    }

    @FXML
    private void confirm() {
        confirmed = true;
        closeWindow();
    }

    @FXML
    private void cancel() {
        confirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Artisan> getSelectedArtisans() {
        return selectedArtisans;
    }
}
