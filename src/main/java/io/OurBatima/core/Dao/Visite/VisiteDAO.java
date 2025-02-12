package io.OurBatima.core.Dao.Visite;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Visite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisiteDAO {

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    //Add a new Visite
    public void addVisite(Visite visite) {
        String sql = "INSERT INTO Visite (Id_projet, dateVisite, observations) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, visite.getId_projet());
            pstmt.setDate(2, visite.getDateVisite());
            pstmt.setString(3, visite.getObservations());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Visite ajoutée avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la visite : " + e.getMessage());
        }
    }

    //Get all Visites
    public List<Visite> getAllVisites() {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM Visite";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Visite visite = new Visite(
                        rs.getInt("Id_visite"),
                        rs.getInt("Id_projet"),
                        rs.getString("observations"),
                        rs.getDate("dateVisite")
                );
                visites.add(visite);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des visites : " + e.getMessage());
        }
        return visites;
    }


    //Get a Visite by ID
    public Visite getVisiteById(int idVisite) {
        String sql = "SELECT * FROM Visite WHERE Id_visite = ?";
        Visite visite = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVisite);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                visite = new Visite(
                        rs.getInt("Id_visite"),
                        rs.getInt("Id_projet"),
                        rs.getString("observations"),
                        rs.getDate("dateVisite")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la visite par ID : " + e.getMessage());
        }
        return visite;
    }

    //Update a Visite
    public void updateVisite(Visite visite) {
        String sql = "UPDATE Visite SET Id_projet = ?, dateVisite = ?, observations = ? WHERE Id_visite = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, visite.getId_projet());
            pstmt.setDate(2, visite.getDateVisite());
            pstmt.setString(3, visite.getObservations());
            pstmt.setInt(4, visite.getId_visite());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Visite mise à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la visite : " + e.getMessage());
        }
    }

    //Delete a Visite
    public void deleteVisite(int idVisite) {
        String sql = "DELETE FROM Visite WHERE Id_visite = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVisite);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Visite supprimée avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la visite : " + e.getMessage());
        }
    }
}
