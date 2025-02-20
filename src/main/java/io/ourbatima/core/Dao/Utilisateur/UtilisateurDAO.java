package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {

    public enum Role {
        Artisan, Constructeur, GestionnaireStock, Admin, Client

    }

    public enum Statut {
        actif, inactif, en_attente

    }

    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public Utilisateur verifierIdentifiants(String email, String motDePasse) {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String motDePasseEnBase = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(motDePasse, motDePasseEnBase)) {
                    return mapUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            handleException("Erreur lors de l'authentification", e);
        }
        return null;
    }

        private Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
            return new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    Statut.valueOf(rs.getString("statut")),
                    rs.getBoolean("isConfirmed"),
                    Role.valueOf(rs.getString("role"))
            );
        }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM Utilisateur WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            handleException("Erreur de vérification email", e);
            return false;
        }
    }

    public static boolean saveUser(Utilisateur utilisateur) {
        Connection conn = null;
        PreparedStatement pstmtUtilisateur = null;
        PreparedStatement pstmtArtisan = null;
        PreparedStatement pstmtConstructeur = null;
        PreparedStatement pstmtClient = null;
        PreparedStatement pstmtGestionnaireDeStock = null;



        boolean isSaved = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction

            // Insérer l'utilisateur dans la table Utilisateur
            String sqlUtilisateur = "INSERT INTO Utilisateur (nom, prenom, email, telephone, role, adresse, mot_de_passe, statut, isConfirmed) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmtUtilisateur = conn.prepareStatement(sqlUtilisateur, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtUtilisateur.setString(1, utilisateur.getNom());
            pstmtUtilisateur.setString(2, utilisateur.getPrenom());
            pstmtUtilisateur.setString(3, utilisateur.getEmail());
            pstmtUtilisateur.setString(4, utilisateur.getTelephone());
            pstmtUtilisateur.setString(5, utilisateur.getRole().name());
            pstmtUtilisateur.setString(6, utilisateur.getAdresse());
            pstmtUtilisateur.setString(7, utilisateur.getMotDePasse());
            pstmtUtilisateur.setString(8, utilisateur.getStatut().name());
            pstmtUtilisateur.setBoolean(9, utilisateur.isConfirmed());

            int rowsAffected = pstmtUtilisateur.executeUpdate();

            if (rowsAffected > 0) {
                // Si l'utilisateur a bien été inséré, on récupère son ID
                ResultSet rs = pstmtUtilisateur.getGeneratedKeys();
                if (rs.next()) {
                    int utilisateurId = rs.getInt(1); // ID généré pour l'utilisateur

                    // Si l'utilisateur est un artisan, insérer ses informations dans la table Artisan
                    if (utilisateur.getRole() == Utilisateur.Role.Artisan) {
                        Artisan artisan = utilisateur.getArtisan();
                        String sqlArtisan = "INSERT INTO Artisan (artisan_id, specialite, salaire_heure) VALUES (?, ?, ?)";
                        pstmtArtisan = conn.prepareStatement(sqlArtisan);
                        pstmtArtisan.setInt(1, utilisateurId); // Utiliser l'ID généré
                        pstmtArtisan.setString(2, artisan.getSpecialite().name()); // Specialité de l'artisan
                        pstmtArtisan.setDouble(3, artisan.getSalaireHeure()); // Salaire horaire de l'artisan

                        int rowsAffectedArtisan = pstmtArtisan.executeUpdate();
                        if (rowsAffectedArtisan > 0) {
                            // Si l'insertion de l'artisan réussit, on commit la transaction
                            conn.commit();
                            isSaved = true;
                        } else {
                            // Rollback si l'insertion de l'artisan échoue
                            conn.rollback();
                        }
                    }else if (utilisateur.getRole() == Utilisateur.Role.Constructeur){
                        Constructeur constructeur = utilisateur.getConstructeur();
                        String sqlConstructeur = "INSERT INTO Constructeur (constructeur_id, specialite, salaire_heure) VALUES (?, ?, ?)";
                        pstmtConstructeur = conn.prepareStatement(sqlConstructeur);
                        pstmtConstructeur.setInt(1, utilisateurId); // Utiliser l'ID généré
                        pstmtConstructeur.setString(2, constructeur.getSpecialite());
                        pstmtConstructeur.setDouble(3, constructeur.getSalaireHeure());

                        int rowsAffectedConstructeur = pstmtConstructeur.executeUpdate();
                        if (rowsAffectedConstructeur > 0) {
                            // Si l'insertion de l'artisan réussit, on commit la transaction
                            conn.commit();
                            isSaved = true;
                        } else {
                            // Rollback si l'insertion de l'artisan échoue
                            conn.rollback();
                        }
                    }else if (utilisateur.getRole() == Utilisateur.Role.Client){
                        Client client = utilisateur.getClient();
                        String sqlClient = "INSERT INTO Client (client_id) VALUES (?)";
                        pstmtClient = conn.prepareStatement(sqlClient);
                        pstmtClient.setInt(1, utilisateurId); // Utiliser l'ID généré


                        int rowsAffectedClient = pstmtClient.executeUpdate();
                        if (rowsAffectedClient > 0) {
                            conn.commit();
                            isSaved = true;
                        } else {
                            conn.rollback();
                        }
                    }else if (utilisateur.getRole() == Utilisateur.Role.GestionnaireStock){
                        GestionnaireDeStock gestionnaireDeStock = utilisateur.getGestionnaireDeStock();
                        String sqlGestionnaireDeStock = "INSERT INTO Gestionnairestock (gestionnairestock_id) VALUES (?)";
                        pstmtGestionnaireDeStock = conn.prepareStatement(sqlGestionnaireDeStock);
                        pstmtGestionnaireDeStock.setInt(1, utilisateurId); // Utiliser l'ID généré


                        int rowsAffectedGestionnaireDeStock = pstmtGestionnaireDeStock.executeUpdate();
                        if (rowsAffectedGestionnaireDeStock > 0) {
                            conn.commit();
                            isSaved = true;
                        } else {
                            conn.rollback();
                        }
                    }
                }
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback si une erreur survient
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmtUtilisateur != null) pstmtUtilisateur.close();
                if (pstmtArtisan != null) pstmtArtisan.close();
                if (pstmtConstructeur != null) pstmtConstructeur.close();

                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isSaved;
    }

    public static boolean updateUser(Utilisateur utilisateur) {
        Connection conn = null;
        PreparedStatement pstmtUtilisateur = null;
        PreparedStatement pstmtArtisan = null;
        PreparedStatement pstmtConstructeur = null;

        boolean isUpdated = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction

            // 🟢 Mise à jour de l'utilisateur
            String sqlUtilisateur = "UPDATE Utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, role = ?, adresse = ?, mot_de_passe = ?, statut = ?, isConfirmed = ? WHERE id = ?";
            pstmtUtilisateur = conn.prepareStatement(sqlUtilisateur);
            pstmtUtilisateur.setString(1, utilisateur.getNom());
            pstmtUtilisateur.setString(2, utilisateur.getPrenom());
            pstmtUtilisateur.setString(3, utilisateur.getEmail());
            pstmtUtilisateur.setString(4, utilisateur.getTelephone());
            pstmtUtilisateur.setString(5, utilisateur.getRole().name());
            pstmtUtilisateur.setString(6, utilisateur.getAdresse());
            pstmtUtilisateur.setString(7, utilisateur.getMotDePasse());
            pstmtUtilisateur.setString(8, utilisateur.getStatut().name());
            pstmtUtilisateur.setBoolean(9, utilisateur.isConfirmed());
            pstmtUtilisateur.setInt(10, utilisateur.getId());

            int rowsAffectedUtilisateur = pstmtUtilisateur.executeUpdate();
            System.out.println("Mise à jour Utilisateur : " + rowsAffectedUtilisateur + " ligne(s) affectée(s)");

            if (rowsAffectedUtilisateur > 0) {
                // 🟢 Mise à jour de l'Artisan si l'utilisateur est un Artisan
                if (utilisateur.getRole() == Utilisateur.Role.Artisan) {
                    Artisan artisan = utilisateur.getArtisan();
                    if (artisan == null) {
                        System.out.println("⚠️ Erreur : utilisateur.getArtisan() est null !");
                        conn.rollback();
                        return false;
                    }

                    String sqlArtisan = "UPDATE Artisan SET specialite = ?, salaire_heure = ? WHERE artisan_id = ?";
                    pstmtArtisan = conn.prepareStatement(sqlArtisan);
                    pstmtArtisan.setString(1, artisan.getSpecialite().name());
                    pstmtArtisan.setDouble(2, artisan.getSalaireHeure());
                    pstmtArtisan.setInt(3, utilisateur.getId()); // ID de l'utilisateur doit correspondre à l'artisan

                    int rowsAffectedArtisan = pstmtArtisan.executeUpdate();
                    System.out.println("Mise à jour Artisan : " + rowsAffectedArtisan + " ligne(s) affectée(s)");

                    if (rowsAffectedArtisan > 0) {
                        conn.commit();
                        isUpdated = true;
                    } else {
                        conn.rollback();
                        System.out.println("⚠️ Aucun artisan mis à jour !");
                    }
                }else if (utilisateur.getRole() == Utilisateur.Role.Constructeur){
                    Constructeur constructeur = utilisateur.getConstructeur();
                    if (constructeur == null) {
                        System.out.println("⚠️ Erreur : utilisateur.getConstructeur() est null !");
                        conn.rollback();
                        return false;
                    }

                    String sqlConstructeur = "UPDATE Constructeur SET specialite = ?, salaire_heure = ? WHERE constructeur_id = ?";
                    pstmtConstructeur = conn.prepareStatement(sqlConstructeur);
                    pstmtConstructeur.setString(1, constructeur.getSpecialite());
                    pstmtConstructeur.setDouble(2, constructeur.getSalaireHeure());
                    pstmtConstructeur.setInt(3, utilisateur.getId());

                    int rowsAffectedConstructeur= pstmtConstructeur.executeUpdate();
                    System.out.println("Mise à jour Constructeur : " + rowsAffectedConstructeur + " ligne(s) affectée(s)");

                    if (rowsAffectedConstructeur > 0) {
                        conn.commit();
                        isUpdated = true;
                    } else {
                        conn.rollback();
                        System.out.println("⚠️ Aucun artisan mis à jour !");
                    }


                }
                else {
                    conn.commit();
                    isUpdated = true;
                }
            } else {
                conn.rollback();
                System.out.println("⚠️ Aucun utilisateur mis à jour !");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmtUtilisateur != null) pstmtUtilisateur.close();
                if (pstmtArtisan != null) pstmtArtisan.close();
                if (pstmtConstructeur != null) pstmtConstructeur.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isUpdated;
    }

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate(); // Récupère le nombre de lignes supprimées

            return rowsDeleted > 0; // Retourne `true` si au moins une ligne a été supprimée
        }
    }



    public boolean updateStatutUtilisateur(int id, Statut statut) {
        String sql = "UPDATE Utilisateur SET statut = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut.name());
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Erreur de mise à jour statut", e);
            return false;
        }
    }

    public Utilisateur getUserById(int id) {
        return getUserByField("id", String.valueOf(id));
    }

    public Utilisateur getUserByEmail(String email) {
        return getUserByField("email", email);
    }

    private Utilisateur getUserByField(String field, String value) {
        String sql = "SELECT * FROM Utilisateur WHERE " + field + " = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() ? mapUtilisateur(rs) : null;
        } catch (SQLException e) {
            handleException("Erreur de récupération utilisateur", e);
            return null;
        }
    }


    private void setCommonParameters(PreparedStatement pstmt, Utilisateur user)
            throws SQLException {
        pstmt.setString(1, user.getNom());
        pstmt.setString(2, user.getPrenom());
        pstmt.setString(3, user.getTelephone());
        pstmt.setString(4, user.getAdresse());
        pstmt.setString(5, user.getMotDePasse());
    }

    private void handleException(String message, SQLException e) {
        System.err.println(message + " : " + e.getMessage());
        e.printStackTrace();
    }

    public int getClientByEmail(String email) {
        // SQL query to fetch client by email (no role filtering)
        String sql = "SELECT id FROM Utilisateur WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Return the client id
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching client by email: " + e.getMessage());
        }
        return -1; // Return -1 if no client found with the provided email
    }

}