package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Dashboard extends ActionView {

    @FXML
    private LineChart<String, Number> plannificationChart;
    @FXML
    private ComboBox<Utilisateur> artisanComboBox;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button artisanButton;

    private final String[] colors = {"#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#A133FF"};

    public void initialize() {
        yAxis.setTickUnit(1); // Correction de l'axe Y pour afficher des entiers
    }

    @FXML
    public void showArtisans() {
        List<Utilisateur> artisans = getUsersFromDatabase("Artisan");
        artisanComboBox.setItems(FXCollections.observableArrayList(artisans));
        artisanComboBox.setVisible(true);
    }

    private List<Utilisateur> getUsersFromDatabase(String role) {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT id, nom FROM Utilisateur WHERE role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nom");
                users.add(new Utilisateur(id, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @FXML
    private void onGetCourbeClicked() {
        Utilisateur selectedArtisan = artisanComboBox.getValue();
        if (selectedArtisan != null) {
            loadChartData(selectedArtisan.getNom());
        }
    }

    private void loadChartData(String artisanName) {
        plannificationChart.getData().clear(); // Effacer l'ancien graphique

        String query = """
                SELECT p.date_planifiee AS date, COUNT(*) AS count
                FROM Plannification p
                JOIN Tache t ON p.id_tache = t.id_tache
                JOIN Utilisateur u ON t.artisan_id = u.id
                WHERE p.statut = 'Terminé' AND u.nom = ?
                GROUP BY p.date_planifiee
                ORDER BY p.date_planifiee;
                """;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(artisanName);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, artisanName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");
                int count = rs.getInt("count");
                series.getData().add(new XYChart.Data<>(date, count));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        plannificationChart.getData().add(series);

        // Appliquer la couleur à la courbe
        Platform.runLater(() -> {
            if (series.getNode() != null) {
                series.getNode().setStyle("-fx-stroke: " + colors[new Random().nextInt(colors.length)] + ";");
            }
        });
    }
}
