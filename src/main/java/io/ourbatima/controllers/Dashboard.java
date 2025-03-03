package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.interfaces.ActionView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Dashboard extends ActionView {

    @FXML
    private LineChart<String, Number> plannificationChart;

    // Colors assigned to artisans
    private final String[] colors = {
            "#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#A133FF", "#33FFF5",
            "#F5A623", "#8B4513", "#FFD700", "#32CD32"
    };

    public void initialize() {
        loadChartData();
    }

    @FXML
    private void onReloadClicked() {
        loadChartData(); // Reload chart when button is clicked
    }

    private void loadChartData() {
        Map<String, Map<String, Integer>> data = getCompletedPlannificationsByArtisan();

        plannificationChart.getData().clear(); // Clear previous data

        int colorIndex = 0; // Track Artisan color
        Map<String, Integer> offsets = new HashMap<>(); // To handle overlapping values

        for (Map.Entry<String, Map<String, Integer>> artisanEntry : data.entrySet()) {
            String artisanName = artisanEntry.getKey();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(artisanName);

            Map<String, Integer> dateCounts = artisanEntry.getValue();
            for (Map.Entry<String, Integer> entry : dateCounts.entrySet()) {
                String date = entry.getKey();
                int count = entry.getValue();

                // If multiple artisans have the same value on the same date, add a small offset
                int offset = offsets.getOrDefault(date, 0);
                offsets.put(date, offset + 1);

                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(date, count + (offset * 0.1)); // Add slight offset
                series.getData().add(dataPoint);
            }

            plannificationChart.getData().add(series);

            // Apply color after UI updates
            int finalColorIndex = colorIndex;
            Platform.runLater(() -> {
                if (series.getNode() != null) {
                    series.getNode().setStyle("-fx-stroke: " + colors[finalColorIndex % colors.length] + ";");
                }
                for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                    if (dataPoint.getNode() != null) {
                        dataPoint.getNode().setStyle("-fx-background-color: " + colors[finalColorIndex % colors.length] + ", white;");
                    }
                }
            });

            colorIndex++; // Move to next color
        }
    }

    private Map<String, Map<String, Integer>> getCompletedPlannificationsByArtisan() {
        Map<String, Map<String, Integer>> result = new HashMap<>();

        String query = """
                SELECT u.nom AS artisan, p.date_planifiee AS date, COUNT(*) AS count
                FROM Plannification p
                JOIN Tache t ON p.id_tache = t.id_tache
                JOIN Utilisateur u ON t.artisan_id = u.id
                WHERE p.statut = 'Terminé' AND u.role = 'Artisan'
                GROUP BY u.nom, p.date_planifiee
                ORDER BY p.date_planifiee;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String artisan = rs.getString("artisan");
                String date = rs.getString("date");
                int count = rs.getInt("count");

                result.putIfAbsent(artisan, new LinkedHashMap<>()); // Preserve order
                result.get(artisan).put(date, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
