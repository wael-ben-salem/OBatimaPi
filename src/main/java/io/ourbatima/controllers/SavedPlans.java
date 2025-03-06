package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.controllers.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SavedPlans extends ActionView {

    @FXML
    private TilePane tilePaneSavedPlans;

    @FXML
    private Button btnReload;

    @FXML
    public void initialize() {
        loadSavedPlans();
    }

    @FXML
    private void onReloadClicked() {
        loadSavedPlans();
    }

    private void loadSavedPlans() {
        tilePaneSavedPlans.getChildren().clear();

        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) {
            System.err.println("No user logged in!");
            return;
        }

        String sql = "SELECT p.id_plannification, t.description, p.date_planifiee " +
                "FROM SavedPlannification sp " +
                "JOIN Plannification p ON sp.plannification_id = p.id_plannification " +
                "JOIN Tache t ON p.id_tache = t.id_tache " +
                "WHERE sp.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUser.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_plannification");
                    String description = rs.getString("description");
                    String date = rs.getString("date_planifiee");

                    VBox card = createPlanCard(id, description, date);
                    tilePaneSavedPlans.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Database error: " + e.getMessage());
        }
    }

    private VBox createPlanCard(int id, String description, String date) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 12; -fx-border-radius: 10; "
                + "-fx-background-radius: 10; -fx-border-color: #6D4C41; -fx-border-width: 1; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 2, 2);");

        Label title = new Label("📌 Plan #" + id);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");

        Label descLabel = new Label("📝 " + description);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);

        Label dateLabel = new Label("📅 " + date);
        dateLabel.setStyle("-fx-text-fill: #6D4C41; -fx-font-size: 12px;");

        // Add delete button
        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("-fx-background-color: #B71C1C; -fx-text-fill: white; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        deleteBtn.setOnAction(e -> handleDeletePlan(id, card));

        HBox buttonBox = new HBox(deleteBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, descLabel, dateLabel, buttonBox);
        return card;
    }

    private void handleDeletePlan(int planId, VBox card) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Saved Plan");
        confirmation.setContentText("Are you sure you want to remove this plan from your saved items?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteFromDatabase(planId)) {
                tilePaneSavedPlans.getChildren().remove(card);
                showSuccessAlert("Plan successfully removed from saved items!");
            }
        }
    }

    private boolean deleteFromDatabase(int planId) {
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) return false;

        String sql = "DELETE FROM SavedPlannification WHERE user_id = ? AND plannification_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, planId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            showErrorAlert("Database error: " + e.getMessage());
            return false;
        }
    }

    private void showErrorAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    private void showSuccessAlert(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }
}