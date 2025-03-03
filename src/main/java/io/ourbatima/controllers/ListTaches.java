package io.ourbatima.controllers;

import io.ourbatima.core.Dao.taches.TacheDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.services.TextToSpeechService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class ListTaches extends ActionView {

    @FXML
    private TilePane tilePaneTaches;

    @FXML
    public void initialize() {
        refreshList();
    }

    @FXML
    public void refreshList() {
        tilePaneTaches.getChildren().clear();
        loadTaches();
    }

    private void loadTaches() {
        String sql = "SELECT t.id_tache, t.description, t.etat, t.date_debut, t.date_fin, "
                + "c.nom AS constructeur_nom, c.prenom AS constructeur_prenom, "
                + "a.nom AS artisan_nom, a.prenom AS artisan_prenom "
                + "FROM tache t "
                + "LEFT JOIN Utilisateur c ON t.constructeur_id = c.id AND c.role = 'Constructeur' "
                + "LEFT JOIN Utilisateur a ON t.artisan_id = a.id AND a.role = 'Artisan'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                count++;
                String constructeur = rs.getString("constructeur_nom") + " " + rs.getString("constructeur_prenom");
                String artisan = rs.getString("artisan_nom") + " " + rs.getString("artisan_prenom");

                constructeur = (constructeur.trim().isEmpty() || constructeur.equals("null null")) ? "Non assigné" : constructeur;
                artisan = (artisan.trim().isEmpty() || artisan.equals("null null")) ? "Non assigné" : artisan;

                VBox card = createTaskCard(
                        rs.getInt("id_tache"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        rs.getString("date_debut"),
                        rs.getString("date_fin") != null ? rs.getString("date_fin") : "N/A",
                        constructeur,
                        artisan
                );
                tilePaneTaches.getChildren().add(card);
            }
            System.out.println("Loaded " + count + " tasks.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTaskCard(int id, String description, String etat, String dateDebut, String dateFin, String constructeur, String artisan) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefSize(230, 160);

        Label lblTitle = new Label("Tâche #" + id);
        lblTitle.setFont(Font.font("Arial", 16));
        lblTitle.setStyle("-fx-font-weight: bold;");

        Label lblDescription = new Label("📌 " + description);
        lblDescription.setFont(Font.font("Arial", 12));
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(180);

        Label lblEtat = new Label("📋 État: " + etat);
        lblEtat.setFont(Font.font("Arial", 12));
        lblEtat.setTextFill("terminé".equalsIgnoreCase(etat) ? Color.GREEN : Color.RED);

        Label lblDates = new Label("📅 " + dateDebut + " → " + dateFin);
        lblDates.setFont(Font.font("Arial", 12));

        Label lblConstructeur = new Label("👷 " + constructeur);
        Label lblArtisan = new Label("🔨 " + artisan);

        // Vocal Icon
        ImageView vocalIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/speaker.png")));
        vocalIcon.setFitWidth(20);
        vocalIcon.setFitHeight(20);
        vocalIcon.setOnMouseClicked(e -> TextToSpeechService.speak(description));

        HBox descriptionBox = new HBox(5, lblDescription, vocalIcon);
        descriptionBox.setStyle("-fx-alignment: center-left;");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center;");

        Button updateButton = new Button("📝 Update");
        updateButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-min-width: 80px; -fx-max-width: 80px;");
        updateButton.setOnAction(e -> openUpdatePopup(id));

        Button deleteButton = new Button("❌ Delete");
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-min-width: 80px; -fx-max-width: 80px;");
        deleteButton.setOnAction(e -> deleteTask(id));

        buttonBox.getChildren().addAll(updateButton, deleteButton);

        card.getChildren().addAll(lblTitle, descriptionBox, lblEtat, lblDates, lblConstructeur, lblArtisan, buttonBox);
        return card;
    }

    private void deleteTask(int idTache) {
        TacheDAO tacheDAO = new TacheDAO();
        tacheDAO.supprimerTache(idTache);
        refreshList();
    }

    private void openUpdatePopup(int idTache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/update_task.fxml"));
            Parent root = loader.load();

            UpdateTask controller = loader.getController();
            controller.setTaskId(idTache);

            Stage stage = new Stage();
            stage.setTitle("Update Task");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gototasks(ActionEvent actionEvent) {
        context.routes().nav("AddTask");
    }
}