package io.ourbatima.core.Dao.Stock;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FournisseurDAO {

    // Méthode pour établir une connexion à la base de données
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Méthode pour enregistrer un fournisseur dans la base de données
    public boolean saveFournisseur(Fournisseur fournisseur) {
        String sql = "INSERT INTO Fournisseur (nom, prenom, email, numero_de_telephone, adresse) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getPrenom());
            pstmt.setString(3, fournisseur.getEmail());
            pstmt.setString(4, fournisseur.getNumeroDeTelephone());
            pstmt.setString(5, fournisseur.getAdresse());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Si l'insertion est réussie, retourne true
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement du fournisseur : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour récupérer un fournisseur par son ID
    public Fournisseur getFournisseurById(int id) {
        String sql = "SELECT * FROM Fournisseur WHERE fournisseur_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Fournisseur(
                        rs.getInt("fournisseur_id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("numero_de_telephone"),
                        rs.getString("adresse")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du fournisseur par ID : " + e.getMessage());
        }
        return null;  // Aucun fournisseur trouvé
    }

    // Méthode pour mettre à jour un fournisseur
    public boolean updateFournisseur(Fournisseur fournisseur) {
        String sql = "UPDATE Fournisseur SET nom = ?, prenom = ?, email = ?, numero_de_telephone = ?, adresse = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getPrenom());
            pstmt.setString(3, fournisseur.getEmail());
            pstmt.setString(4, fournisseur.getNumeroDeTelephone());
            pstmt.setString(5, fournisseur.getAdresse());
            pstmt.setInt(6, fournisseur.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the update was successful
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du fournisseur : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour vérifier si un fournisseur existe déjà par email
    public boolean isFournisseurExist(String email) {
        String sql = "SELECT 1 FROM Fournisseur WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if the fournisseur exists
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du fournisseur : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour supprimer un fournisseur par ID
    public boolean deleteFournisseur(int id) {
        String sql = "DELETE FROM Fournisseur WHERE fournisseur_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the deletion was successful
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur : " + e.getMessage());
            return false;
        }
    }
}