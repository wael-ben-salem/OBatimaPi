package io.OurBatima.core.Dao.Utilisateur;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {

    // Méthode pour établir une connexion à la base de données
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Méthode pour vérifier les identifiants (email et mot de passe)
    public Utilisateur verifierIdentifiants(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";

        try (Connection conn = connect();  // Connexion automatique fermée
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            // Vérification du mot de passe
            if (rs.next()) {
                String motDePasseEnBase = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(motDePasse, motDePasseEnBase)) {
                    // Si le mot de passe correspond, retourner l'utilisateur
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            rs.getString("statut"),
                            rs.getBoolean("is_confirmed"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution de la requête : " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Aucun utilisateurs trouvé ou mot de passe incorrect
    }

    // Méthode pour vérifier si un email existe déjà dans la base de données
    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM utilisateurs WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour enregistrer un utilisateur dans la base de données
    public boolean saveUser(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, telephone, adresse, statut, is_confirmed, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getPrenom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, utilisateur.getMotDePasse());
            pstmt.setString(5, utilisateur.getTelephone());
            pstmt.setString(6, utilisateur.getAdresse());
            pstmt.setString(7, utilisateur.getStatut());
            pstmt.setBoolean(8, utilisateur.isConfirmed());
            pstmt.setString(9, utilisateur.getRole());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Si l'insertion est réussie, retourne true
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour activer ou désactiver un utilisateur en fonction de son statut
    public boolean updateStatutUtilisateur(int id, String statut) {
        String sql = "UPDATE utilisateurs SET statut = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour récupérer un utilisateur par son ID
    public Utilisateur getUserById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getString("statut"),
                        rs.getBoolean("is_confirmed"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur par ID : " + e.getMessage());
        }
        return null;  // Aucun utilisateur trouvé
    }
    public boolean updateUser(Utilisateur user) {
        String sql = "UPDATE utilisateurs SET "
                + "nom = ?, prenom = ?, telephone = ?, adresse = ?, mot_de_passe = ? "
                + "WHERE email = ?"; // Vérifiez le nom de la colonne email

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getTelephone());
            pstmt.setString(4, user.getAdresse());
            pstmt.setString(5, user.getMotDePasse());
            pstmt.setString(6, user.getEmail()); // Index 6 pour WHERE email

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Lignes modifiées : " + rowsAffected); // Debug

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur SQL updateUser: " + e.getMessage()); // Log détaillé
            e.printStackTrace();
            return false;
        }
    }
    public Utilisateur getUserByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getString("statut"),
                        rs.getBoolean("is_confirmed"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur par ID : " + e.getMessage());
        }
        return null;  // Aucun utilisateur trouvé
    }
}
