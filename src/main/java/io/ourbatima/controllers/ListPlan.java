package io.ourbatima.controllers;

import io.ourbatima.core.Dao.plannification.PlannificationDAO;
import io.ourbatima.core.interfaces.ActionView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        tilePanePlans.setStyle("-fx-padding: 30 0 0 0; -fx-hgap: 15; -fx-vgap: 20;"); // 30px top padding, 15px horizontal gap, 20px vertical gap
        refreshList();
    }




    @FXML
    public void refreshList() {
        tilePanePlans.getChildren().clear();

        // Add a spacer at the top to push cards down
        Label spacer = new Label();
        spacer.setMinHeight(30);
        tilePanePlans.getChildren().add(spacer);

        loadPlans();
    }

    private void loadPlans() {
        String sql = "SELECT p.id_plannification, t.description AS tache_description, p.date_planifiee, " +
                "p.heure_debut, p.remarques, p.statut " +
                "FROM Plannification p " +
                "JOIN Tache t ON p.id_tache = t.id_tache";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                count++;

                int id = rs.getInt("id_plannification");
                String description = rs.getString("tache_description");
                String date = rs.getString("date_planifiee");
                String heure = rs.getString("heure_debut");
                String remarques = rs.getString("remarques");
                String statut = rs.getString("statut");

                // Ensure values are not null
                description = (description != null) ? description : "Aucune description";
                date = (date != null) ? date : "Non défini";
                heure = (heure != null) ? heure : "Non défini";
                remarques = (remarques != null) ? remarques : "Aucune remarque";
                statut = (statut != null) ? statut : "Planifié"; // Default statut

                VBox card = createPlanCard(id, description, date, heure, remarques, statut);
                tilePanePlans.getChildren().add(card);
            }
            System.out.println("Loaded " + count + " plans.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createPlanCard(int id, String description, String date, String heure, String remarques, String statut) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefSize(230, 160);

        // Add top margin to space out each card
        card.setTranslateY(20); // Push each card down

        Label lblTitle = new Label("Plan #" + id);
        lblTitle.setFont(Font.font("Arial", 16));
        lblTitle.setStyle("-fx-font-weight: bold;");

        Label lblDescription = new Label("📝 " + description);
        lblDescription.setFont(Font.font("Arial", 12));
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(210);

        Label lblDate = new Label("📅 " + date + " - " + heure);
        lblDate.setFont(Font.font("Arial", 12));

        Label lblRemarques = new Label("📌 " + remarques);
        lblRemarques.setFont(Font.font("Arial", 12));

        Label lblStatut = new Label("📊 Statut: " + statut);
        lblStatut.setFont(Font.font("Arial", 12));
        lblStatut.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getStatutColor(statut) + ";");

        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center;");

        Button updateButton = new Button("✏️ Edit");
        updateButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
        updateButton.setOnAction(e -> openUpdatePopup(id));

        Button deleteButton = new Button("❌ Delete");
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deletePlan(id));

        buttonBox.getChildren().addAll(updateButton, deleteButton);
        card.getChildren().addAll(lblTitle, lblDescription, lblDate, lblRemarques, lblStatut, buttonBox);

        return card;
    }
    private String getStatutColor(String statut) {
        switch (statut) {
            case "Planifié": return "#FFA500"; // Orange
            case "En cours": return "#008000"; // Green
            case "Terminé": return "#0000FF"; // Blue
            default: return "#000000"; // Black (default)
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
}
