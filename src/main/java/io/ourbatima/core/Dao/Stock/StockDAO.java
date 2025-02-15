package io.ourbatima.core.Dao.Stock;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDAO {

    // Méthode pour établir une connexion à la base de données
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Méthode pour enregistrer un stock dans la base de données
    public boolean saveStock(Stock stock) {
        String sql = "INSERT INTO stock (nom, emplacement, dateCreation) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stock.getNom());
            pstmt.setString(2, stock.getEmplacement());
            pstmt.setString(3, stock.getDateCreation());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Si l'insertion est réussie, retourne true
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement du stock : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour récupérer un stock par son ID
    public Stock getStockById(int id) {
        String sql = "SELECT * FROM stock WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Stock(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("emplacement"),
                        rs.getString("dateCreation")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du stock par ID : " + e.getMessage());
        }
        return null;  // Aucun stock trouvé
    }

    // Méthode pour mettre à jour un stock
    public boolean updateStock(Stock stock) {
        String sql = "UPDATE stock SET nom = ?, emplacement = ?, dateCreation = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stock.getNom());
            pstmt.setString(2, stock.getEmplacement());
            pstmt.setString(3, stock.getDateCreation());
            pstmt.setInt(4, stock.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour vérifier si un stock existe déjà par nom
    public boolean isStockExist(String nom) {
        String sql = "SELECT 1 FROM stock WHERE nom = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du stock : " + e.getMessage());
            return false;
        }
    }



    public boolean deleteStock(int id) {
        String sql = "DELETE FROM stock WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the deletion was successful
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du stock : " + e.getMessage());
            return false;
        }
    }
}