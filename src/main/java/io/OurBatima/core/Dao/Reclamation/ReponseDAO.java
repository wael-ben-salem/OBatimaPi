package io.OurBatima.core.Dao.Reclamation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Reponse;

public class ReponseDAO {
    private Connection conn;

    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    // Ajouter une réponse
    public boolean add(Reponse reponse) {
        String SQL = "INSERT INTO reponse_1 (reclamation_id, description, statut, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, reponse.getReclamationId());
            stmt.setString(2, reponse.getDescription());
            stmt.setString(3, reponse.getStatut());
            stmt.setTimestamp(4, Timestamp.valueOf(reponse.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Mettre à jour une réponse
    public void update(Reponse reponse) {
        String SQL = "UPDATE reponse_1 SET description = ?, statut = ?, date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, reponse.getDescription());
            stmt.setString(2, reponse.getStatut());
            stmt.setTimestamp(3, Timestamp.valueOf(reponse.getDate()));
            stmt.setInt(4, reponse.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Supprimer une réponse
    public void delete(int id) {
        String SQL = "DELETE FROM reponse_1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Récupérer toutes les réponses
    public List<Reponse> getAll() {
        String SQL = "SELECT * FROM reponse_1";
        List<Reponse> reponses = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                Reponse reponse = new Reponse();
                reponse.setId(rs.getInt("id"));
                reponse.setReclamationId(rs.getInt("reclamation_id"));
                reponse.setDescription(rs.getString("description"));
                reponse.setStatut(rs.getString("statut"));
                reponse.setDate(rs.getTimestamp("date").toLocalDateTime());
                reponses.add(reponse);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reponses;
    }
    public void deleteByReclamationId(int reclamationId) {
        String SQL = "DELETE FROM reponse_1 WHERE reclamation_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, reclamationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
