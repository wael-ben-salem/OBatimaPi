package io.ourbatima.controllers;

import io.ourbatima.core.Dao.plannification.PlannificationDAO;
import io.ourbatima.core.Dao.plannification.SavedPlannificationDAO;
import io.ourbatima.core.interfaces.ActionView;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class ListPlan extends ActionView {


    @FXML
    private TilePane tilePanePlans;

    @FXML
    public void initialize() {
        tilePanePlans.setStyle("-fx-padding: 20 15 15 15; " +
                "-fx-hgap: 18; " +
                "-fx-vgap: 18; " +
                "-fx-background-color: white;");
        refreshList();
    }

    @FXML
    public void refreshList() {
        tilePanePlans.getChildren().clear();
        loadPlans();
    }

    private void loadPlans() {
        String sql = "SELECT p.id_plannification, t.description AS tache_description, " +
                "p.date_planifiee, p.heure_debut, p.remarques, p.statut, p.rating " +
                "FROM Plannification p " +
                "JOIN Tache t ON p.id_tache = t.id_tache";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_plannification");
                String description = rs.getString("tache_description");
                String date = rs.getString("date_planifiee");
                String heure = rs.getString("heure_debut");
                String remarques = rs.getString("remarques");
                String statut = rs.getString("statut");
                int rating = rs.getInt("rating");

                VBox card = createPlanCard(id, description, date, heure, remarques, statut, rating);
                tilePanePlans.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createPlanCard(int id, String description, String date, String heure, String remarques, String statut, int rating) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setStyle("-fx-background-color: white; " +
                "-fx-padding: 14; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #6D4C41; " + // Cacao Brown Border
                "-fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 3, 3);");
        card.setPrefSize(260, 200);

        Label lblTitle = new Label("Plan #" + id);
        lblTitle.setFont(Font.font("Arial", 16));
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #6D4C41;"); // Cacao Brown

        Label lblDescription = new Label("📝 " + description);
        lblDescription.setFont(Font.font("Arial", 12));
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(230);
        lblDescription.setStyle("-fx-text-fill: black;");

        Label lblDate = new Label("📅 " + date + " - " + heure);
        lblDate.setFont(Font.font("Arial", 12));
        lblDate.setStyle("-fx-text-fill: black;");

        Label lblRemarques = new Label("📌 " + remarques);
        lblRemarques.setFont(Font.font("Arial", 12));
        lblRemarques.setStyle("-fx-text-fill: black;");

        Label lblStatut = new Label("📊 Statut: " + statut);
        lblStatut.setFont(Font.font("Arial", 12));
        lblStatut.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getStatutColor(statut) + ";");

        HBox ratingBox = createRatingStars(id, rating);

        HBox buttonBox = new HBox(12);
        buttonBox.setStyle("-fx-alignment: center;");
        buttonBox.setSpacing(8);

        Button updateButton = new Button("✏️ Modifier");
        updateButton.setStyle("-fx-background-color: #6D4C41; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8;");
        updateButton.setOnAction(e -> openUpdatePopup(id));

        Button deleteButton = new Button("🗑 Supprimer");
        deleteButton.setStyle("-fx-background-color: #B71C1C; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8;");
        deleteButton.setOnAction(e -> deletePlan(id));

        Button saveButton = new Button("⭐");
        saveButton.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: black; -fx-border-radius: 8; -fx-background-radius: 8;");
        saveButton.setOnAction(e -> savePlannification(id));

        Label saveStatus = new Label();
        updateSaveStatus(saveStatus, id);

        buttonBox.getChildren().addAll(updateButton, deleteButton, saveButton, saveStatus);
        card.getChildren().addAll(lblTitle, lblDescription, lblDate, lblRemarques, lblStatut, ratingBox, buttonBox);

        return card;
    }

    private void updateSaveStatus(Label label, int planId) {
        SavedPlannificationDAO savedDAO = new SavedPlannificationDAO();
        boolean isSaved = savedDAO.isPlannificationSaved(planId);
        label.setText(isSaved ? "⭐ Saved!" : "");
    }

    private void savePlannification(int id) {
        SavedPlannificationDAO savedDAO = new SavedPlannificationDAO();
        if (savedDAO.savePlannification(id)) {
            refreshList(); // Refresh to show new status
        }
    }

    private HBox createRatingStars(int planId, int currentRating) {
        HBox starsBox = new HBox(6);
        starsBox.setStyle("-fx-alignment: center;");

        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(getStarImage(i <= currentRating));
            star.setFitWidth(22);
            star.setFitHeight(22);
            final int rating = i;
            star.setOnMouseClicked(e -> updateRating(planId, rating));
            starsBox.getChildren().add(star);
        }

        return starsBox;
    }

    private Image getStarImage(boolean filled) {
        String imagePath = filled ? "/star_filled.png" : "/star_empty.png";
        return new Image(getClass().getResourceAsStream(imagePath));
    }

    private void updateRating(int planId, int rating) {
        String sql = "UPDATE Plannification SET rating = ? WHERE id_plannification = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating);
            stmt.setInt(2, planId);
            stmt.executeUpdate();
            refreshList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getStatutColor(String statut) {
        switch (statut) {
            case "Planifié": return "#FFA500";
            case "En cours": return "#008000";
            case "Terminé": return "#0000FF";
            default: return "#000000";
        }
    }

    private void deletePlan(int idPlan) {
        PlannificationDAO plannificationDAO = new PlannificationDAO();
        plannificationDAO.supprimerPlannification(idPlan);
        refreshList();
    }



private void openUpdatePopup(int idPlan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/update_plan.fxml"));
            Parent root = loader.load();

            UpdatePlan updatePlanController = loader.getController();
            updatePlanController.setPlanId(idPlan);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Plan");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gotoplanifs(ActionEvent actionEvent) {
        context.routes().nav("AddPlan");
    }

    public void gotochat(ActionEvent actionEvent) {
        context.routes().nav("Chatbot");
    }

    public void gotoDashboard(ActionEvent actionEvent) {
        context.routes().nav("Dashboard");
    }

    public void gotoWeather(ActionEvent actionEvent) {
        context.routes().nav("Weather");
    }
}