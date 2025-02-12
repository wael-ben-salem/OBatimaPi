package io.OurBatima.core.Dao.EtapeProjet;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.EtapeProjet;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EtapeProjetDAO {

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Add a new EtapeProjet
    public void addEtapeProjet(EtapeProjet etapeProjet) {
        String sql = "INSERT INTO EtapeProjet (Id_projet, nomEtape, description, dateDebut, dateFin, statut, montant) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, etapeProjet.getId_projet());
            pstmt.setString(2, etapeProjet.getNomEtape());
            pstmt.setString(3, etapeProjet.getDescription());
            pstmt.setDate(4, etapeProjet.getDteDebut());
            pstmt.setDate(5, etapeProjet.getDatefin());
            pstmt.setString(6, etapeProjet.getStatut());
            pstmt.setBigDecimal(7, etapeProjet.getMontant());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Etape du projet inséré avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de l'étape du projet: " + e.getMessage());
        }
    }

    // Get all EtapeProjets
    public List<EtapeProjet> getAllEtapeProjets() {
        List<EtapeProjet> etapeProjets = new ArrayList<>();
        String sql = "SELECT * FROM EtapeProjet";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EtapeProjet etapeProjet = new EtapeProjet(
                        rs.getInt("Id_etapeProjet"),
                        rs.getInt("Id_projet"),
                        rs.getString("nomEtape"),
                        rs.getString("description"),
                        rs.getDate("dateDebut"),
                        rs.getDate("dateFin"),
                        rs.getString("statut"),
                        rs.getBigDecimal("montant")
                );
                etapeProjets.add(etapeProjet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'étape du projet: " + e.getMessage());
        }
        return etapeProjets;
    }

    // Get an EtapeProjet by ID
    public EtapeProjet getEtapeProjetById(int idEtapeProjet) {
        String sql = "SELECT * FROM EtapeProjet WHERE Id_etapeProjet = ?";
        EtapeProjet etapeProjet = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEtapeProjet);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                etapeProjet = new EtapeProjet(
                        rs.getInt("Id_etapeProjet"),
                        rs.getInt("Id_projet"),
                        rs.getString("nomEtape"),
                        rs.getString("description"),
                        rs.getDate("dateDebut"),
                        rs.getDate("dateFin"),
                        rs.getString("statut"),
                        rs.getBigDecimal("montant")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'étape du projet par ID: " + e.getMessage());
        }
        return etapeProjet;
    }

    // Update an EtapeProjet
    public void updateEtapeProjet(EtapeProjet etapeProjet) {
        String sql = "UPDATE EtapeProjet SET Id_projet = ?, nomEtape = ?, description = ?, dateDebut = ?, dateFin = ?, statut = ?, montant = ? WHERE Id_etapeProjet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, etapeProjet.getId_projet());
            pstmt.setString(2, etapeProjet.getNomEtape());
            pstmt.setString(3, etapeProjet.getDescription());
            pstmt.setDate(4, etapeProjet.getDteDebut());
            pstmt.setDate(5, etapeProjet.getDatefin());
            pstmt.setString(6, etapeProjet.getStatut());
            pstmt.setBigDecimal(7, etapeProjet.getMontant());
            pstmt.setInt(8, etapeProjet.getId_etapeProjet());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Etape du projet mis à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'étape du projet: " + e.getMessage());
        }
    }

    // Delete an EtapeProjet
    public void deleteEtapeProjet(int idEtapeProjet) {
        String sql = "DELETE FROM EtapeProjet WHERE Id_etapeProjet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEtapeProjet);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Etape du projet supprimée avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'étape du projet : " + e.getMessage());
        }
    }
}
