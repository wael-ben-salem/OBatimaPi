package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {



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
            Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    Utilisateur.Statut.valueOf(rs.getString("statut")), // Utiliser l'enum de la classe Utilisateur
                    rs.getBoolean("isConfirmed"),
                    Utilisateur.Role.valueOf(rs.getString("role")) // Utiliser l'enum de la classe Utilisateur
            );

            // Chargement des sous-entités selon le rôle
            int userId = rs.getInt("id");
            switch(utilisateur.getRole()) {
                case Artisan:
                    utilisateur.setArtisan(getArtisanByUserId(userId));
                    break;
                case Constructeur:
                    utilisateur.setConstructeur(getConstructeurByUserId(userId));
                    break;
                case Client:
                    utilisateur.setClient(getClientByUserId(userId));
                    break;
                case GestionnaireStock:
                    utilisateur.setGestionnaireDeStock(getGestionnaireStockByUserId(userId));
                    break;
            }
            return utilisateur;
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
                        Artisan artisan = (Artisan) utilisateur;  // L'instance sera assurément un Artisan ici

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
                        Constructeur constructeur = (Constructeur) utilisateur;  // L'instance sera assurément un Artisan ici
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
                        if (utilisateur instanceof GestionnaireDeStock) {

                            String sqlGestionnaireDeStock = "INSERT INTO gestionnairestock (gestionnairestock_id) VALUES (?)";
                            pstmtGestionnaireDeStock = conn.prepareStatement(sqlGestionnaireDeStock);
                            pstmtGestionnaireDeStock.setInt(1, utilisateurId); // Utiliser l'ID généré


                            int rowsAffectedGestionnaireDeStock = pstmtGestionnaireDeStock.executeUpdate();
                            if (rowsAffectedGestionnaireDeStock > 0) {
                                conn.commit();
                                isSaved = true;
                            } else {
                                conn.rollback();
                            }
                        }else {
                            System.out.println("L'utilisateur n'est pas un gestionnaire de stock.");

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
    public boolean updateUser(Utilisateur user) {
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false); // Début de la transaction

            // 1. Récupérer le rôle actuel de l'utilisateur
            Utilisateur existingUser = getUserById(user.getId(), conn);
            Utilisateur.Role oldRole = existingUser.getRole();
            Utilisateur.Role newRole = user.getRole();

            // 2. Mettre à jour les données principales de l'utilisateur (y compris le rôle)
            updateMainUserData(user, conn);

            // 3. Gérer les transitions de rôles
            if (oldRole != newRole) {
                // Supprimer l'entrée de l'ancien rôle
                deleteOldRoleData(existingUser, conn);

                // Créer une entrée pour le nouveau rôle si nécessaire
                insertNewRoleData(user, conn);
            } else {
                // Mettre à jour les données spécifiques au rôle existant
                updateExistingRoleData(user, conn);
            }

            conn.commit(); // Valider la transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
            handleException("Erreur lors de la mise à jour", e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); }
                catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // Méthodes auxiliaires
    private void updateMainUserData(Utilisateur user, Connection conn) throws SQLException {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, telephone=?, adresse=?, mot_de_passe=?, role=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getTelephone());
            pstmt.setString(4, user.getAdresse());
            pstmt.setString(5, user.getMotDePasse());
            pstmt.setString(6, user.getRole().name());
            pstmt.setInt(7, user.getId());
            pstmt.executeUpdate();
        }
    }

    private void deleteOldRoleData(Utilisateur existingUser, Connection conn) throws SQLException {
        switch (existingUser.getRole()) {
            case Artisan:
                deleteFromTable("Artisan", "artisan_id", existingUser.getId(), conn);
                break;
            case Constructeur:
                deleteFromTable("Constructeur", "constructeur_id", existingUser.getId(), conn);
                break;
            case GestionnaireStock:
                deleteFromTable("GestionnaireStock", "gestionnaire_id", existingUser.getId(), conn);
                break;
            case Client:
                deleteFromTable("Client", "client_id", existingUser.getId(), conn);
                break;
            // Ajouter d'autres rôles si nécessaire
        }
    }

    private void insertNewRoleData(Utilisateur user, Connection conn) throws SQLException {
        switch (user.getRole()) {
            case Artisan:
                insertArtisan(user.getId(), user.getArtisan(), conn);
                break;
            case Constructeur:
                insertConstructeur(user.getId(), user.getConstructeur(), conn);
                break;
            case GestionnaireStock:
                insertGestionnaireStock(user.getId(), user.getGestionnaireDeStock(), conn);
                break;
            case Client:
                insertCliet(user.getId(), user.getClient(), conn);
                break;
        }
    }

    private void updateExistingRoleData(Utilisateur user, Connection conn) throws SQLException {
        switch (user.getRole()) {
            case Artisan:
                updateArtisan(user.getId(), user.getArtisan(), conn);
                break;
            case Constructeur:
                updateConstructeur(user.getId(), user.getConstructeur(), conn);
                break;

        }
    }

    // Méthodes génériques pour les opérations sur les tables
    private void deleteFromTable(String tableName, String idColumn, int userId, Connection conn) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, idColumn);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    private void insertArtisan(int userId, Artisan artisan, Connection conn) throws SQLException {
        String sql = "INSERT INTO Artisan (artisan_id, specialite, salaire_heure) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, artisan.getSpecialite().name());
            pstmt.setDouble(3, artisan.getSalaireHeure());
            pstmt.executeUpdate();
        }
    }

    private void insertConstructeur(int userId, Constructeur constructeur, Connection conn) throws SQLException {
        String sql = "INSERT INTO Constructeur (constructeur_id, specialite, salaire_heure) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, constructeur.getSpecialite());
            pstmt.setDouble(3, constructeur.getSalaireHeure());
            pstmt.executeUpdate();
        }
    }
    private void insertGestionnaireStock(int userId, GestionnaireDeStock gestionnaireDeStock, Connection conn) throws SQLException {
        String sql = "INSERT INTO gestionnairestock (gestionnairestock_id) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            pstmt.executeUpdate();
        }
    }
    private void insertCliet(int userId, Client client, Connection conn) throws SQLException {
        String sql = "INSERT INTO Client (client_id) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            pstmt.executeUpdate();
        }
    }

    // Méthodes de mise à jour existantes (gardées pour les rôles inchangés)
    private void updateArtisan(int userId, Artisan artisan, Connection conn) throws SQLException {
        String sql = "UPDATE Artisan SET specialite=?, salaire_heure=? WHERE artisan_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, artisan.getSpecialite().name());
            pstmt.setDouble(2, artisan.getSalaireHeure());
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        }
    }

    private void updateConstructeur(int userId, Constructeur constructeur, Connection conn) throws SQLException {
        String sql = "UPDATE Constructeur SET specialite=?, salaire_heure=? WHERE constructeur_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, constructeur.getSpecialite());
            pstmt.setDouble(2, constructeur.getSalaireHeure());
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        }
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



    public boolean updateStatutUtilisateur(int id, Utilisateur.Statut statut) {
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

    public Utilisateur getUserById(int id, Connection conn) {
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
    public List<Utilisateur> getUsersByRole(Utilisateur.Role role) {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur WHERE role = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Utilisateur user = mapUtilisateur(rs);

                // Forcer le chargement des sous-entités si nécessaire
                switch(role) {
                    case Artisan:
                        user.setArtisan(getArtisanByUserId(user.getId()));
                        break;
                    case Constructeur:
                        user.setConstructeur(getConstructeurByUserId(user.getId()));
                        break;
                }

                users.add(user);
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération par rôle", e);
        }
        return users;
    }
    private Artisan getArtisanByUserId(int userId) {
        String sql = "SELECT * FROM Artisan WHERE artisan_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int artisanId = rs.getInt("artisan_id"); // Récupération de l'ID
                String specialiteStr = rs.getString("specialite"); // Récupération de la spécialité en tant que String
                double salaireHeure = rs.getDouble("salaire_heure"); // Récupération du salaire

                // Conversion de la spécialité en ENUM avec vérification
                Artisan.Specialite specialite = null;
                try {
                    specialite = Artisan.Specialite.valueOf(specialiteStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valeur inconnue pour specialite : " + specialiteStr);
                }

                return new Artisan(artisanId, specialite, salaireHeure);
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération artisan", e);
        }
        return null;
    }
    Constructeur getConstructeurId(int userId) {
        String sql = "SELECT u.nom, u.prenom, c.* "
                + "FROM Utilisateur u "
                + "JOIN Constructeur c ON u.id = c.constructeur_id " // Adaptez la jointure
                + "WHERE c.constructeur_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Constructeur constructeur = new Constructeur();
                // Remplir les champs Utilisateur
                constructeur.setId(rs.getInt("constructeur_id"));
                constructeur.setNom(rs.getString("nom")); // Chargé depuis Utilisateur
                constructeur.setPrenom(rs.getString("prenom"));

                // Remplir les champs spécifiques
                constructeur.setSpecialite(rs.getString("specialite"));
                constructeur.setSalaireHeure(rs.getDouble("salaire_heure"));
                return constructeur;
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération constructeur", e);
        }
        return null;
    }

    GestionnaireDeStock getGestionnaireStockId(int userId) {
        String sql = "SELECT u.nom, u.prenom, g.* "
                + "FROM Utilisateur u "
                + "JOIN gestionnairestock g ON u.id = g.gestionnairestock_id " // Adaptez la jointure
                + "WHERE g.gestionnairestock_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                GestionnaireDeStock gestionnaire = new GestionnaireDeStock();
                // Remplir les champs Utilisateur
                gestionnaire.setId(rs.getInt("gestionnairestock_id"));
                gestionnaire.setNom(rs.getString("nom")); // Chargé depuis Utilisateur
                gestionnaire.setPrenom(rs.getString("prenom"));
                return gestionnaire;
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération gestionnaire", e);
        }
        return null;
    }
    Constructeur getConstructeurByUserId(int userId) {
        String sql = "SELECT * FROM Constructeur WHERE constructeur_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int constructeurId = rs.getInt("constructeur_id"); // Récupération de l'ID
                String specialiteStr = rs.getString("specialite"); // Récupération de la spécialité en tant que String
                double salaireHeure = rs.getDouble("salaire_heure"); // Récupération du salaire


                return new Constructeur(constructeurId, specialiteStr, salaireHeure);
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération constructeur", e);
        }
        return null;
    }

    private GestionnaireDeStock getGestionnaireStockByUserId(int userId) {
        String sql = "SELECT * FROM gestionnairestock WHERE gestionnairestock_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int gestionnairestock_id = rs.getInt("gestionnairestock_id"); // Récupération de l'ID


                return new GestionnaireDeStock(gestionnairestock_id);
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération GestionnaireStock", e);
        }
        return null;
    }

    private Client getClientByUserId(int userId) {
        String sql = "SELECT * FROM client WHERE client_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int client_id = rs.getInt("client_id"); // Récupération de l'ID


                return new Client(client_id);
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération Client", e);
        }
        return null;

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