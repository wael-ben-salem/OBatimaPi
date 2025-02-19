package io.ourbatima.core.Dao.Rapport;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Rapport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RapportDAO {

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    //Add a new Rapport
    public void addRapport(Rapport rapport) {
        String sql = "INSERT INTO Rapport (Id_etapeProjet, titre, contenu, dateCreation) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rapport.getId_etapeProjet());
            pstmt.setString(2, rapport.getTitre());
            pstmt.setString(3, rapport.getContenu());
            pstmt.setDate(4, rapport.getDateCreation());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Rapport ajouté avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du rapport : " + e.getMessage());
        }
    }

    //Get all Rapports
    public List<Rapport> getAllRapports() {
        List<Rapport> rapports = new ArrayList<>();
        String sql = "SELECT * FROM Rapport";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Rapport rapport = new Rapport(
                        rs.getInt("id"),
                        rs.getInt("Id_etapeProjet"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getDate("dateCreation")
                );
                rapports.add(rapport);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des rapports : " + e.getMessage());
        }
        return rapports;
    }

    //Get a Rapport by ID
    public Rapport getRapportById(int idRapport) {
        String sql = "SELECT * FROM Rapport WHERE id = ?";
        Rapport rapport = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRapport);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                rapport = new Rapport(
                        rs.getInt("id"),
                        rs.getInt("Id_etapeProjet"),
                        rs.getString("titre"),
                        rs.getString("contenu"),
                        rs.getDate("dateCreation")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du rapport par ID : " + e.getMessage());
        }
        return rapport;
    }

    //Update a Rapport
    public void updateRapport(Rapport rapport) {
        String sql = "UPDATE Rapport SET Id_etapeProjet = ?, titre = ?, contenu = ?, dateCreation = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rapport.getId_etapeProjet());
            pstmt.setString(2, rapport.getTitre());
            pstmt.setString(3, rapport.getContenu());
            pstmt.setDate(4, rapport.getDateCreation());
            pstmt.setInt(5, rapport.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Rapport mis à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du rapport : " + e.getMessage());
        }
    }

    //Delete a Rapport
    public void deleteRapport(int idRapport) {
        String sql = "DELETE FROM Rapport WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRapport);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Rapport supprimé avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du rapport : " + e.getMessage());
        }
    }
}
