package io.ourbatima.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class GeminiService {

    private static final String GEMINI_API_KEY = "AIzaSyCwoIf6m-Eyf-iK4nYlbT6kGW6MIrJnEgA";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

    private Connection connection;

    public GeminiService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetches actual data from the specified table.
     */
    private String getTableData(String tableName) throws SQLException {
        StringBuilder output = new StringBuilder();
        String query = "SELECT * FROM " + tableName + " LIMIT 5"; // Limit to first 5 rows for clarity

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Column headers
            output.append("Table: ").append(tableName).append("\nColumns: ");
            for (int i = 1; i <= columnCount; i++) {
                output.append(metaData.getColumnName(i)).append(" (").append(metaData.getColumnTypeName(i)).append("), ");
            }
            output.append("\nData:\n");

            // Fetch actual data
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    output.append(rs.getString(i)).append(" | ");
                }
                output.append("\n");
            }
        }
        return output.toString();
    }

    /**
     * Fetches planned tasks and their associated users.
     */
    private String getPlannedTasksWithUsers() throws SQLException {
        StringBuilder output = new StringBuilder();
        String query = "SELECT DISTINCT " +
                "ua.id AS artisan_id, ua.nom AS artisan_nom, ua.prenom AS artisan_prenom, " +
                "uc.id AS constructeur_id, uc.nom AS constructeur_nom, uc.prenom AS constructeur_prenom " +
                "FROM Plannification p " +
                "JOIN Tache t ON p.id_tache = t.id_tache " +
                "LEFT JOIN Utilisateur ua ON t.artisan_id = ua.id AND ua.role = 'Artisan' " +
                "LEFT JOIN Utilisateur uc ON t.constructeur_id = uc.id AND uc.role = 'Constructeur' " +
                "WHERE p.statut = 'Planifié';";


        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            output.append("Planned Tasks with Users:\n");
            output.append("Artisan | Constructeur\n");

            while (rs.next()) {
                String artisan = rs.getString("artisan_nom") + " " + rs.getString("artisan_prenom");
                String constructeur = rs.getString("constructeur_nom") + " " + rs.getString("constructeur_prenom");
                output.append(artisan).append(" | ").append(constructeur).append("\n");
            }
        }
        return output.toString();
    }

    public String getAnswer(String question) {
        try {
            // Get actual table data
            String dataTache = getTableData("tache");
            String dataPlannification = getTableData("plannification");
            String plannedTasks = getPlannedTasksWithUsers();

            // Updated prompt with real data
            String prompt = "Here are database tables with their structures and data:\n"
                    + dataTache + "\n"
                    + dataPlannification + "\n"
                    + "Here are artisans and constructeurs with planned tasks:\n"
                    + plannedTasks + "\n"
                    + "Based on all this analyse it all,and answer the following user question directly:\n"
                    + question;

            // Send request to Gemini API
            JsonObject requestBody = new JsonObject();
            JsonArray contentsArray = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray partsArray = new JsonArray();
            JsonObject partObject = new JsonObject();

            partObject.addProperty("text", prompt);
            partsArray.add(partObject);
            content.add("parts", partsArray);
            contentsArray.add(content);
            requestBody.add("contents", contentsArray);

            HttpURLConnection conn = (HttpURLConnection) new URL(GEMINI_API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(new Gson().toJson(requestBody).getBytes());
            }

            // Read Gemini API response
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP Error: " + responseCode);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = new Gson().fromJson(br, JsonObject.class);
                return response.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return "⚠ Erreur: " + e.getMessage();
        }
    }
}