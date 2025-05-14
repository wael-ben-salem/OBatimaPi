package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.controllers.FaceAuthenticator;
import io.ourbatima.controllers.FaceEncryption;
import io.ourbatima.controllers.MessagingService;
import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UtilisateurDAO {



    private static Connection connect() throws SQLException {
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

                // Normaliser le format du hash pour jBCrypt
                String normalizedHash = motDePasseEnBase;
                if (motDePasseEnBase != null && motDePasseEnBase.startsWith("$2y$")) {
                    normalizedHash = "$2a$" + motDePasseEnBase.substring(4);
                }

                if (BCrypt.checkpw(motDePasse, normalizedHash)) {
                    return mapUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            handleException("Erreur lors de l'authentification", e);
        }
        return null;
    }
    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
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
        utilisateur.setFaceData(rs.getBytes("face_data"));


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
        PreparedStatement pstmtRole = null;
        boolean isSaved = false;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into Utilisateur table
            String sqlUtilisateur = "INSERT INTO Utilisateur (nom, prenom, email, telephone, role, adresse, mot_de_passe, statut, isConfirmed, face_data) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

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
            pstmtUtilisateur.setBytes(10, utilisateur.getFaceData());



            int rowsAffected = pstmtUtilisateur.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmtUtilisateur.getGeneratedKeys();
                if (rs.next()) {
                    int utilisateurId = rs.getInt(1);
                    utilisateur.setId(utilisateurId); // Set generated ID

                    // Insert into role-specific table
                    int roleSpecificId = -1; // ID spécifique au rôle
                    switch (utilisateur.getRole()) {
                        case Artisan:
                            Artisan artisan = utilisateur.getArtisan();
                            if (artisan == null || artisan.getSpecialite() == null) {
                                throw new IllegalArgumentException("La spécialité de l'artisan est requise.");
                            }
                            String sqlArtisan = "INSERT INTO Artisan (artisan_id, specialite, salaire_heure) VALUES (?, ?, ?)";
                            pstmtRole = conn.prepareStatement(sqlArtisan);
                            pstmtRole.setInt(1, utilisateurId);
                            pstmtRole.setString(2, artisan.getSpecialite().name());
                            pstmtRole.setDouble(3, artisan.getSalaireHeure());
                            roleSpecificId = utilisateurId; // Artisan utilise l'ID utilisateur
                            break;

                        case Constructeur:
                            Constructeur constructeur = utilisateur.getConstructeur();
                            if (constructeur == null || constructeur.getSpecialite() == null) {
                                throw new IllegalArgumentException("La spécialité du constructeur est requise.");
                            }
                            String sqlConstructeur = "INSERT INTO Constructeur (constructeur_id, specialite, salaire_heure) VALUES (?, ?, ?)";
                            pstmtRole = conn.prepareStatement(sqlConstructeur);
                            pstmtRole.setInt(1, utilisateurId);
                            pstmtRole.setString(2, constructeur.getSpecialite());
                            pstmtRole.setDouble(3, constructeur.getSalaireHeure());
                            roleSpecificId = utilisateurId; // Constructeur utilise l'ID utilisateur
                            break;

                        case Client:
                            String sqlClient = "INSERT INTO Client (client_id) VALUES (?)";
                            pstmtRole = conn.prepareStatement(sqlClient);
                            pstmtRole.setInt(1, utilisateurId);
                            roleSpecificId = utilisateurId; // Client utilise l'ID utilisateur
                            break;

                        case GestionnaireStock:
                            String sqlGestionnaire = "INSERT INTO gestionnairestock (gestionnairestock_id) VALUES (?)";
                            pstmtRole = conn.prepareStatement(sqlGestionnaire);
                            pstmtRole.setInt(1, utilisateurId);
                            roleSpecificId = utilisateurId; // Gestionnaire utilise l'ID utilisateur
                            break;
                    }

                    if (pstmtRole != null) {
                        int roleRowsAffected = pstmtRole.executeUpdate();
                        if (roleRowsAffected > 0) {
                            conn.commit(); // Commit transaction
                            isSaved = true;

                            // Créer le compte de messagerie avec l'ID spécifique au rôle
                            new MessagingService().createUserMessagingAccount(utilisateur, roleSpecificId);
                        } else {
                            conn.rollback(); // Rollback if role insert fails
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
            handleException("Erreur lors de la sauvegarde", e);
        } finally {
            // Close resources
            try {
                if (pstmtRole != null) pstmtRole.close();
                if (pstmtUtilisateur != null) pstmtUtilisateur.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSaved;
    }    public boolean updateUser(Utilisateur user) {
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false); // Début de la transaction

            System.out.println("Tentative de récupération de l'utilisateur avec l'ID: " + user.getId());
            Utilisateur existingUser = getUserById(user.getId(), conn);
            if (existingUser == null) {
                System.out.println("Aucun utilisateur trouvé avec l'ID: " + user.getId());
                throw new SQLException("Utilisateur non trouvé avec l'ID: " + user.getId());
            }
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
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, telephone=?, adresse=?, mot_de_passe=?, role=?, face_data=?, WHERE id=?";        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getTelephone());
            pstmt.setString(4, user.getAdresse());
            pstmt.setString(5, user.getMotDePasse());
            pstmt.setString(6, user.getRole().name());
            pstmt.setBytes(7, user.getFaceData());

            pstmt.setInt(8, user.getId());

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

    public Utilisateur getUserById(int userId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = new Utilisateur();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setTelephone(rs.getString("telephone"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setMotDePasse(rs.getString("mot_de_passe"));
                    user.setRole(Utilisateur.Role.valueOf(rs.getString("role"))); // Assurez-vous que le rôle est correctement mappé
                    return user;
                } else {
                    return null; // Aucun utilisateur trouvé avec cet ID
                }
            }
        }
    }

    public Utilisateur getUserProjById(int id) {
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

    public static void handleException(String message, SQLException e) {
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
    public static Artisan getArtisanByUserId(int userId) {
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
    public Constructeur getConstructeurId(int constructeurId) throws SQLException {

        String sql = "SELECT u.*, c.specialite, c.salaire_heure "
                + "FROM Utilisateur u "
                + "INNER JOIN Constructeur c ON u.id = c.constructeur_id "
                + "WHERE c.constructeur_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, constructeurId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Création du Constructeur
                return new Constructeur(
                        constructeurId,
                        new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("mot_de_passe"),
                                rs.getString("telephone"),
                                rs.getString("adresse"),
                                Utilisateur.Statut.valueOf(rs.getString("statut")),
                                rs.getBoolean("isConfirmed"),
                                Utilisateur.Role.Constructeur

                        ),
                        rs.getString("specialite"),
                        rs.getDouble("salaire_heure")
                );
            } else {
                throw new SQLException("Constructeur introuvable pour ID: " + constructeurId);
            }
        }
    }
    public Artisan getArtisanId(int userId) {
        String sql = "SELECT u.nom, u.prenom, a.* "
                + "FROM Utilisateur u "
                + "JOIN Artisan a ON u.id = a.artisan_id " // Adaptez la jointure
                + "WHERE a.artisan_id = ?";


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Artisan artisan = new Artisan();
                // Remplir les champs Utilisateur
                artisan.setArtisan_id(rs.getInt("artisan_id")); // ✅ Initialise Constructeur.constructeur_id
                artisan.setNom(rs.getString("nom")); // Chargé depuis Utilisateur
                artisan.setPrenom(rs.getString("prenom"));

                // Remplir les champs spécifiques
                artisan.setSpecialite(Artisan.Specialite.valueOf(rs.getString("specialite")));
                artisan.setSalaireHeure(rs.getDouble("salaire_heure"));
                return artisan;
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération artisan", e);
        }
        return null; }
    public GestionnaireDeStock getGestionnaireStockId(int userId) {
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
                gestionnaire.setGestionnairestock_id(rs.getInt("gestionnairestock_id")); // ✅ Initialise Constructeur.constructeur_id
                gestionnaire.setNom(rs.getString("nom")); // Chargé depuis Utilisateur
                gestionnaire.setPrenom(rs.getString("prenom"));


                return gestionnaire;
            }
        } catch (SQLException e) {
            handleException("Erreur de récupération gestionnaire", e);
        }
        return null;
    }
    public static Constructeur getConstructeurByUserId(int userId) {
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

    public static GestionnaireDeStock getGestionnaireStockByUserId(int userId) {
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

    private static Client getClientByUserId(int userId) {
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
        return -1;
    }
    public List<Integer> getAllConstructeurIds() throws SQLException {
        return getIdsByRole(Utilisateur.Role.Constructeur);
    }

    public List<Integer> getAllGestionnaireStockIds() throws SQLException {
        return getIdsByRole(Utilisateur.Role.GestionnaireStock);
    }
    public List<Integer> getAllArtisanIds() throws SQLException {
        return getIdsByRole(Utilisateur.Role.Artisan);
    }

    private List<Integer> getIdsByRole(Utilisateur.Role role) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM Utilisateur WHERE role = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        }
        return ids;
    }
    public void saveFaceData(int userId, byte[] faceData) throws SQLException {
        try {
            byte[] encryptedData = FaceEncryption.encrypt(faceData);
            System.out.println("[DEBUG] Taille des données avant chiffrement : " + faceData.length);
            System.out.println("[DEBUG] Taille des données après chiffrement : " + encryptedData.length);

            String sql = "UPDATE Utilisateur SET face_data = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBytes(1, encryptedData);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new SQLException("Erreur de chiffrement des données faciales", e);
        }
    }
    public byte[] getFaceData(int userId) throws SQLException {
        String sql = "SELECT face_data FROM Utilisateur WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                byte[] faceData = rs.getBytes("face_data");
                System.out.println("[DEBUG] Taille des données récupérées : " + (faceData != null ? faceData.length + " octets" : "null"));
                return faceData;
            }
        }
        return null;
    }
    public Utilisateur authenticateByFace(byte[] liveFaceData) throws SQLException {
        if (liveFaceData == null || liveFaceData.length == 0) {
            System.err.println("Aucune donnée faciale fournie");
            return null;
        }

        String sql = "SELECT * FROM Utilisateur WHERE face_data IS NOT NULL";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                byte[] storedEncrypted = rs.getBytes("face_data");
                if (storedEncrypted == null) continue;

                try {
                    byte[] storedDecrypted = FaceEncryption.decrypt(storedEncrypted);
                    if (FaceAuthenticator.compareFaces(storedDecrypted, liveFaceData)) {
                        System.out.println("[DEBUG] Utilisateur trouvé : " + rs.getString("email"));
                        return mapUtilisateur(rs);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur de déchiffrement pour l'utilisateur " + rs.getInt("id") + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'authentification faciale", e);
        }
        System.out.println("[DEBUG] Aucun utilisateur correspondant trouvé.");
        return null;
    }
    public void saveFaceTemplate(int userId, byte[] template) {
        String sql = "UPDATE Utilisateur SET face_data = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, template);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Erreur sauvegarde visage", e);
        }
    }

    public void checkFaceData(int userId) throws SQLException {
        String sql = "SELECT face_data FROM Utilisateur WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                byte[] faceData = rs.getBytes("face_data");
                System.out.println("[DEBUG] Données faciales stockées pour l'utilisateur " + userId + " : " + (faceData != null ? faceData.length + " octets" : "null"));
            }
        }
    }

    public Optional<Utilisateur> getClientById(int id) {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roleStr = rs.getString("role");
                    Utilisateur.Role role = Utilisateur.Role.valueOf(roleStr);
                    Utilisateur.Statut statut = Utilisateur.Statut.valueOf(rs.getString("statut"));

                    if (role == Utilisateur.Role.Client) {
                        return Optional.of(new Client(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("telephone"),
                                rs.getString("adresse")
                        ));
                    } else {
                        return Optional.of(new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("mot_de_passe"),
                                rs.getString("telephone"),
                                rs.getString("adresse"),
                                statut,
                                rs.getBoolean("isConfirmed"),
                                role
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }




    public boolean userExists(int userId) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeQuery().next();
        }
    }
    public boolean createPasswordResetToken(String email, String token, LocalDateTime expiry) {
        String sql = "UPDATE Utilisateur SET reset_token = ?, reset_token_expiry = ? WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            pstmt.setObject(2, expiry);
            pstmt.setString(3, email);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            handleException("Erreur lors de la création du token de réinitialisation", e);
            return false;
        }
    }
    public Utilisateur validatePasswordResetToken(String token) {
        String sql = "SELECT * FROM Utilisateur WHERE reset_token = ? AND reset_token_expiry > NOW()";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapUtilisateur(rs);
            }
        } catch (SQLException e) {
            handleException("Erreur lors de la validation du token", e);
        }
        return null;
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE Utilisateur SET mot_de_passe = ?, reset_token = NULL, reset_token_expiry = NULL WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            handleException("Erreur lors de la mise à jour du mot de passe", e);
            return false;
        }
    }
    public List<Utilisateur> findUsersByFace(byte[] liveFaceData) throws SQLException {
        List<Utilisateur> matchingUsers = new ArrayList<>();

        if (liveFaceData == null || liveFaceData.length == 0) {
            System.err.println("Aucune donnée faciale fournie");
            return matchingUsers; // Retourne une liste vide si les données faciales sont invalides
        }

        String sql = "SELECT * FROM Utilisateur WHERE face_data IS NOT NULL";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                byte[] storedEncrypted = rs.getBytes("face_data");
                if (storedEncrypted == null) continue;

                try {
                    // Déchiffrer les données faciales stockées
                    byte[] storedDecrypted = FaceEncryption.decrypt(storedEncrypted);

                    // Comparer les données faciales
                    if (FaceAuthenticator.compareFaces(storedDecrypted, liveFaceData)) {
                        // Si les visages correspondent, ajouter l'utilisateur à la liste
                        Utilisateur user = mapUtilisateur(rs);
                        matchingUsers.add(user);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur de déchiffrement pour l'utilisateur " + rs.getInt("id") + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la recherche des utilisateurs par visage", e);
        }

        return matchingUsers;
    }
    public boolean updateUserPassword(String email, String hashedPassword) {
        String sql = "UPDATE utilisateur SET password = ?, reset_password_token = NULL WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
            return false;
        }
    }

    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setMotDePasse(rs.getString("mot_de_passe"));
                // Ajoutez les autres champs...
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

}