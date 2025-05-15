package io.ourbatima.core.Dao.Reclamation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.ourbatima.core.Dao.DatabaseConnection;

import io.ourbatima.core.model.Reponse;



public class ReponseDAO {

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection(); // Assuming DatabaseConnection is a utility class for connection
    }

    // Add a new Reponse
    public int addReponse(Reponse reponse) {
        String sql = "INSERT INTO Reponse (description, statut, date, Id_Reclamation) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reponse.getDescription());
            pstmt.setString(2, reponse.getStatut());
            pstmt.setTimestamp(3, Timestamp.valueOf(reponse.getDate()));
            pstmt.setInt(4, reponse.getIdReclamation());
            pstmt.executeUpdate();


            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la reponse : " + e.getMessage());
        }
        return -1;
    }

    // Get all Reponses
    public List<Reponse> getAllReponses() {
        List<Reponse> Reponses = new ArrayList<>();
        String sql = "SELECT * FROM reponse";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reponses.add(new Reponse(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getInt("id_Reclamation")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des reponse : " + e.getMessage());
        }
        return Reponses;
    }



    // Update a Reponse
    public void updateReponse(Reponse reponse) {
        String sql = "UPDATE reponse SET description = ?, statut = ?, date = ?, id_reclamation = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reponse.getDescription());
            pstmt.setString(2, reponse.getStatut());
            pstmt.setTimestamp(3, Timestamp.valueOf(reponse.getDate()));
            pstmt.setInt(4, reponse.getIdReclamation());
            pstmt.setInt(5, reponse.getId());

            // Debug output
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: " +
                    "description=" + reponse.getDescription() + ", " +
                    "statut=" + reponse.getStatut() + ", " +
                    "date=" + reponse.getDate() + ", " +
                    "id_reclamation=" + reponse.getIdReclamation() + ", " +
                    "id=" + reponse.getId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("✅ Réponse mise à jour avec succès dans la base de données!");
            } else {
                System.out.println("⚠️ Aucune ligne mise à jour. Vérifiez l'ID de la réponse.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la réponse : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Delete a Reponse
    public void deleteReponse(int id) {
        String sql = "DELETE FROM Reponse WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }
    }
    // Get a Reponse by ID
    public Reponse getReponseById(int id) {
        String sql = "SELECT * FROM reponse WHERE id = ?";
        Reponse reponse = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                reponse = new Reponse(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getInt("id_reclamation")// Convert Timestamp to LocalDateTime

                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la reponse par ID: " + e.getMessage());
        }
        return reponse;
    }
    // 🔹 Récupérer les réponses d'une réclamation spécifique
    public List<Reponse> getReponsesByReclamationId(int reclamationId) {
        List<Reponse> reponses = new ArrayList<>();
        String sql = "SELECT * FROM Reponse WHERE id_reclamation = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reclamationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reponses.add(new Reponse(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getString("statut"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getInt("id_reclamation")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des réponses pour la réclamation ID " + reclamationId + " : " + e.getMessage());
        }
        return reponses;
    }

}