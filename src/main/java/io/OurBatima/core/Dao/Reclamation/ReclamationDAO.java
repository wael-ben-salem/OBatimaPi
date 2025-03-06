package io.OurBatima.core.Dao.Reclamation;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Reclamation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ReclamationDAO {

    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Ajouter une réclamation
    public int addReclamation(Reclamation reclamation) {
        String sql = "INSERT INTO Reclamation (description, statut, date, utilisateur_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, reclamation.getDescription());
            pstmt.setString(2, reclamation.getStatut());
            pstmt.setTimestamp(3, Timestamp.valueOf(reclamation.getDate()));
            pstmt.setInt(4, reclamation.getUtilisateurId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réclamation : " + e.getMessage());
        }
        return -1;
    }

    // Récupérer une réclamation par ID
    public Reclamation getReclamationById(int id) {
        String sql = "SELECT * FROM Reclamation WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Reclamation(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getString("statut"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getInt("utilisateur_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réclamation : " + e.getMessage());
        }
        return null;
    }

    // Récupérer toutes les réclamations
    public List<Reclamation> getAllReclamations() {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM Reclamation";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                reclamations.add(new Reclamation(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getInt("utilisateur_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations : " + e.getMessage());
        }
        return reclamations;
    }

    // Mettre à jour une réclamation
    public void updateReclamation(Reclamation reclamation) {
        String sql = "UPDATE Reclamation SET description = ?, statut = ?, date = ?, utilisateur_id = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reclamation.getDescription());
            pstmt.setString(2, reclamation.getStatut());
            pstmt.setTimestamp(3, Timestamp.valueOf(reclamation.getDate()));
            pstmt.setInt(4, reclamation.getUtilisateurId());
            pstmt.setInt(5, reclamation.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la réclamation : " + e.getMessage());
        }
    }

    // Supprimer une réclamation
    public void deleteReclamation(int id) {
        String sql = "DELETE FROM Reclamation WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
        }
    }
    // Récupérer les réclamations d'un utilisateur spécifique
    public List<Reclamation> getReclamationsByUtilisateur(int utilisateurId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM Reclamation WHERE utilisateur_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, utilisateurId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reclamations.add(new Reclamation(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getString("statut"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getInt("utilisateur_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations de l'utilisateur : " + e.getMessage());
        }
        return reclamations;
    }

}
