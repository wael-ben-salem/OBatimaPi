package io.OurBatima.core.Dao.Projet;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Projet;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {

    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Add a new project
    public void addProjet(Projet projet) {
        String sql = "INSERT INTO Projet (nomProjet, Id_equipe, id_client, type, styleArch, budget, etat, dateCreation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, projet.getNomProjet());
            pstmt.setInt(2, projet.getId_equipe());
            pstmt.setInt(3, projet.getId_client());
            pstmt.setString(4, projet.getType());
            pstmt.setString(5, projet.getStyleArch());
            pstmt.setBigDecimal(6, projet.getBudget());
            pstmt.setString(7, projet.getEtat());
            pstmt.setTimestamp(8, projet.getDateCreation());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projet inséré avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion du projet : " + e.getMessage());
        }
    }

    // Get all projects
    public List<Projet> getAllProjets() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM Projet";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Projet projet = new Projet(
                        rs.getInt("Id_projet"),
                        rs.getString("nomProjet"),
                        rs.getInt("Id_equipe"),
                        rs.getInt("id_client"),  // Retrieve id_client
                        rs.getBigDecimal("budget"),
                        rs.getString("type"),
                        rs.getString("styleArch"),
                        rs.getString("etat"),
                        rs.getTimestamp("dateCreation")
                );
                projets.add(projet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des projets : " + e.getMessage());
        }
        return projets;
    }

    // Get a project by ID
    public Projet getProjetById(int idProjet) {
        String sql = "SELECT * FROM Projet WHERE Id_projet = ?";
        Projet projet = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProjet);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                projet = new Projet(
                        rs.getInt("Id_projet"),
                        rs.getString("nomProjet"),
                        rs.getInt("Id_equipe"),
                        rs.getInt("id_client"),
                        rs.getBigDecimal("budget"),
                        rs.getString("type"),
                        rs.getString("styleArch"),
                        rs.getString("etat"),
                        rs.getTimestamp("dateCreation")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du projet par ID : " + e.getMessage());
        }
        return projet;
    }

    // Update a project
    public void updateProjet(Projet projet) {
        String sql = "UPDATE Projet SET Id_equipe = ?, id_client = ?, nomProjet = ?, type = ?, styleArch = ?, budget = ?, etat = ? WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projet.getId_equipe());
            pstmt.setInt(2, projet.getId_client());
            pstmt.setString(3, projet.getNomProjet());
            pstmt.setString(4, projet.getType());
            pstmt.setString(5, projet.getStyleArch());
            pstmt.setBigDecimal(6, projet.getBudget());
            pstmt.setString(7, projet.getEtat());
            pstmt.setInt(8, projet.getId_projet());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projet mis à jour avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du projet : " + e.getMessage());
        }
    }

    // Delete project
    public void deleteProjet(int idProjet) {
        String sql = "DELETE FROM Projet WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProjet);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projet supprimé avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du projet : " + e.getMessage());
        }
    }

    // Assign équipe to project
    public void assignEquipeToProjet(int projetId, int equipeId) {
        String sql = "UPDATE Projet SET Id_equipe = ? WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipeId);
            pstmt.setInt(2, projetId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Équipe " + equipeId + " assignée avec succès au projet " + projetId);
            } else {
                System.out.println("Projet avec l'ID " + projetId + " non trouvé.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'assignation de l'équipe au projet : " + e.getMessage());
        }
    }
}
