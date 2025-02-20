package io.OurBatima.core.Dao.Reclamation;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Reclamation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {
    private Connection conn;

    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Ajouter une réclamation
    public void add(Reclamation reclamation) {
        String SQL = "INSERT INTO reclamation_1 (description, statut, date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, reclamation.getDescription());
            stmt.setString(2, reclamation.getStatut());
            stmt.setTimestamp(3, Timestamp.valueOf(reclamation.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Mettre à jour une réclamatio
    public void update(Reclamation reclamation) {
        String SQL = "UPDATE 'reclamation_1' SET description = ?, statut = ?, date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, reclamation.getDescription());
            stmt.setString(2, reclamation.getStatut());
            stmt.setTimestamp(3, Timestamp.valueOf(reclamation.getDate()));
            stmt.setInt(4, reclamation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Supprimer une réclamation
    public void delete(int id) {
        String SQL = "DELETE FROM reclamation_1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // afficher toutes les réclamations
    public List<Reclamation> getAll() {
        String SQL = "SELECT * FROM reclamation_1";
        List<Reclamation> reclamations = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setDate(rs.getTimestamp("date").toLocalDateTime());
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reclamations;
    }
}
