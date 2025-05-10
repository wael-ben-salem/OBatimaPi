package io.ourbatima.core.Dao.plannification;

import io.ourbatima.core.model.Plannification;
import io.ourbatima.core.Dao.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlannificationDAO {

    public void ajouterPlannification(Plannification plannification) {
        String sql = "INSERT INTO Plannification (id_tache, date_planifiee, heure_debut, heure_fin, priorite, remarques, statut) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, plannification.getIdTache());
            stmt.setDate(2, plannification.getDatePlanifiee());
            stmt.setTime(3, plannification.getHeureDebut());
            stmt.setTime(4, plannification.getHeureFin());
            stmt.setString(5, plannification.getPriorite());
            stmt.setString(6, plannification.getRemarques());
            stmt.setString(7, plannification.getStatut());

            stmt.executeUpdate();
            System.out.println("Plannification ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Plannification> getAllPlannifications() {
        List<Plannification> plannifications = new ArrayList<>();
        String sql = "SELECT * FROM Plannification";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Plannification plannification = new Plannification(
                        rs.getInt("id_plannification"),
                        rs.getInt("id_tache"),
                        rs.getDate("date_planifiee"),
                        rs.getTime("heure_debut"),
                        rs.getTime("heure_fin"),
                        rs.getString("priorite"),
                        rs.getString("remarques"),
                        rs.getString("statut")
                );
                plannifications.add(plannification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plannifications;
    }

    public void supprimerPlannification(int idPlannification) {
        String sql = "DELETE FROM Plannification WHERE id_plannification = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPlannification);
            stmt.executeUpdate();
            System.out.println("Plannification supprimée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlannification(Plannification plannification) {
        String sql = "UPDATE Plannification SET id_tache = ?, date_planifiee = ?, heure_debut = ?, heure_fin = ?, remarques = ?, statut = ? WHERE id_plannification = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, plannification.getIdTache());
            stmt.setDate(2, plannification.getDatePlanifiee());
            stmt.setTime(3, plannification.getHeureDebut());
            stmt.setTime(4, plannification.getHeureFin());
            stmt.setString(5, plannification.getRemarques());
            stmt.setString(6, plannification.getStatut());
            stmt.setInt(7, plannification.getIdPlannification());

            stmt.executeUpdate();
            System.out.println("Plannification mise à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Plannification getPlannificationById(int idPlannification) {
        String sql = "SELECT * FROM Plannification WHERE id_plannification = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPlannification);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Plannification(
                        rs.getInt("id_plannification"),
                        rs.getInt("id_tache"),
                        rs.getDate("date_planifiee"),
                        rs.getTime("heure_debut"),
                        rs.getTime("heure_fin"),
                        rs.getString("priorite"),
                        rs.getString("remarques"),
                        rs.getString("statut")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePlannification(int id) {
        String sql = "INSERT INTO SavedPlannification (user_id, plannification_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id=user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 1);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}