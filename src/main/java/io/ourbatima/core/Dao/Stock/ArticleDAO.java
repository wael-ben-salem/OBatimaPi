package io.ourbatima.core.Dao.Stock;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    // Méthode pour établir une connexion à la base de données
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Méthode pour enregistrer un article dans la base de données
    public boolean saveArticle(Article article) {
        // Base SQL query without Id_etapeProjet
        String sql = "INSERT INTO Article (nom, description, prix_unitaire, photo, stock_id, fournisseur_id";

        // Append Id_etapeProjet to the query if it is not null
        if (article.getEtapeProjetId() != null) {
            sql += ", Id_etapeProjet) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql += ") VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, article.getNom());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getPrixUnitaire());
            pstmt.setString(4, article.getPhoto());
            pstmt.setInt(5, article.getStockId());
            pstmt.setInt(6, article.getFournisseurId());

            // Set Id_etapeProjet only if it is not null
            if (article.getEtapeProjetId() != null) {
                pstmt.setInt(7, article.getEtapeProjetId());
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // If insertion is successful, return true
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'article : " + e.getMessage());
            return false;
        }
    }
    // Méthode pour récupérer un article par son ID
    public Article getArticleById(int id) {
        String sql = "SELECT * FROM Article WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Article(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("prix_unitaire"),
                        rs.getString("photo"),
                        rs.getInt("stock_id"),
                        rs.getInt("fournisseur_id"),
                        rs.getInt("etapeprojet_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'article par ID : " + e.getMessage());
        }
        return null;  // Aucun article trouvé
    }

    // Méthode pour mettre à jour un article
    public boolean updateArticle(Article article) {
        String sql = "UPDATE Article SET nom = ?, description = ?, prix_unitaire = ?, photo = ?, stock_id = ?, fournisseur_id = ?, Id_etapeProjet = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, article.getNom());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getPrixUnitaire());
            pstmt.setString(4, article.getPhoto());
            pstmt.setInt(5, article.getStockId());
            pstmt.setInt(6, article.getFournisseurId());
            pstmt.setInt(7, article.getEtapeProjetId());
            pstmt.setInt(8, article.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the update was successful
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'article : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour supprimer un article par ID
    public boolean deleteArticle(int id) {
        String sql = "DELETE FROM Article WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if the deletion was successful
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article : " + e.getMessage());
            return false;
        }
    }
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM Article";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                articles.add(new Article(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("prix_unitaire"),
                        rs.getString("photo"),
                        rs.getInt("stock_id"),
                        rs.getInt("fournisseur_id"),
                        rs.getInt("Id_etapeProjet")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles : " + e.getMessage());
        }
        return articles;
    }

}
