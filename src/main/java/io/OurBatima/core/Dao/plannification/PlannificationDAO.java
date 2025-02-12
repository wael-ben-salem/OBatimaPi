package io.OurBatima.core.Dao.plannification;

import io.OurBatima.core.model.Plannification;
import io.OurBatima.core.Dao.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlannificationDAO {

    public void ajouterPlannification(Plannification plannification) {
        String sql = "INSERT INTO Plannification (id_tache, date_planifiee, heure_debut, priorite) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, plannification.getIdTache());
            stmt.setDate(2, plannification.getDatePlanifiee()); // Correction ici
            stmt.setTime(3, plannification.getHeurePlannification());
            stmt.setString(4, plannification.getPriorite());

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
                        rs.getDate("date_planifiee"), // Correction ici
                        rs.getTime("heure_debut"), // Correction ici
                        rs.getString("priorite")
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
}
