    package io.ourbatima.core.Dao.Utilisateur;

    import io.ourbatima.controllers.MessagingService;
    import io.ourbatima.core.model.Utilisateur.*;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;


    public class EquipeDAO {
        // Méthode de connexion
        private Connection connect() throws SQLException {
            return getConnection();
        }

        public void create(Equipe equipe) throws SQLException {
            System.out.println("Début de la création de l'équipe : " + equipe.getNom());

            String sql = "INSERT INTO Equipe (nom, constructeur_id, gestionnairestock_id) VALUES (?, ?, ?)";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                conn.setAutoCommit(false); // Début transaction

                // Insertion équipe
                stmt.setString(1, equipe.getNom());
                stmt.setInt(2, equipe.getConstructeur().getConstructeur_id());
                stmt.setInt(3, equipe.getGestionnaireStock().getGestionnairestock_id());
                stmt.executeUpdate();

                // Récupération ID généré
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) equipe.setId(rs.getInt(1));
                }

                // Insertion artisans
                insertArtisans(conn, equipe);


                conn.commit(); // Validation transaction

                System.out.println("Équipe créée avec succès : " + equipe.getNom());

                // Envoi de la notification
                String notification1 = "Vous avez été ajouté à l'équipe " + equipe.getNom();
                String notification2 = "Vous avez rejoint la conversation de l'équipe " + equipe.getNom();

                MessagingService messagingService = new MessagingService();
                messagingService.sendTeamNotification(equipe, notification1);
                messagingService.sendTeamNotification(equipe, notification2);


            } catch (SQLException e) {
                System.out.println("Erreur lors de la création de l'équipe : " + e.getMessage());
                throw new SQLException("Erreur création équipe: " + e.getMessage(), e);
            }
        }

        public void update(Equipe equipe) throws SQLException {
            String sql = "UPDATE Equipe SET nom=?, constructeur_id=?, gestionnairestock_id=? WHERE id=?";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false);

                // Mise à jour équipe
                stmt.setString(1, equipe.getNom());
                stmt.setInt(2, equipe.getConstructeur().getConstructeur_id());
                stmt.setInt(3, equipe.getGestionnaireStock().getGestionnairestock_id());
                stmt.setInt(4, equipe.getId());
                stmt.executeUpdate();

                // Mise à jour artisans
                deleteArtisans(conn, equipe.getId());
                insertArtisans(conn, equipe);

                conn.commit();

            } catch (SQLException e) {
                throw new SQLException("Erreur mise à jour équipe: " + e.getMessage(), e);
            }
        }

        public void delete(int id) throws SQLException {
            String sql = "DELETE FROM Equipe WHERE id=?";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new SQLException("Erreur suppression équipe: " + e.getMessage(), e);
            }
        }

        // Méthodes helpers avec propagation de la connexion
        private void insertArtisans(Connection conn, Equipe equipe) throws SQLException {
            String sql = "INSERT INTO Equipe_Artisan (equipe_id, artisan_id) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Artisan artisan : equipe.getArtisans()) {
                    stmt.setInt(1, equipe.getId());
                    stmt.setInt(2, artisan.getArtisan_id());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        private void deleteArtisans(Connection conn, int equipeId) throws SQLException {
            String sql = "DELETE FROM Equipe_Artisan WHERE equipe_id=?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, equipeId);
                stmt.executeUpdate();
            }
        }

        public List<Equipe> findAll() throws SQLException {
            String sql = "SELECT * FROM Equipe";
            List<Equipe> equipes = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Equipe equipe = mapEquipe(rs, conn);
                    equipes.add(equipe);
                }
            }
            return equipes;
        }

        public Equipe findById(int id) throws SQLException {
            String sql = "SELECT * FROM Equipe WHERE id=?";
            Equipe equipe = null;

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        equipe = mapEquipe(rs, conn);
                        equipe.setArtisans(getArtisansForEquipe(conn, id));
                    }
                }
            } catch (SQLException e) {
                throw new SQLException("Erreur recherche équipe: " + e.getMessage(), e);
            }
            return equipe;
        }

        private List<Artisan> getArtisansForEquipe(Connection conn, int equipeId) throws SQLException {
            String sql = "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, u.telephone, u.adresse, u.role,  " +
                    "a.artisan_id, a.specialite, a.salaire_heure " +
                    "FROM utilisateur u " +
                    "JOIN Artisan a ON u.id = a.artisan_id " +
                    "JOIN Equipe_Artisan ea ON a.artisan_id = ea.artisan_id " +
                    "WHERE ea.equipe_id = ?";

            List<Artisan> artisans = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, equipeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Utilisateur utilisateur = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("mot_de_passe"),
                                rs.getString("telephone"),
                                rs.getString("adresse"),
                                Utilisateur.Statut.actif,
                                true,
                                Utilisateur.Role.valueOf(rs.getString("role"))
                        );

                        Artisan artisan = new Artisan(
                                rs.getInt("artisan_id"),
                                utilisateur,
                                Artisan.Specialite.valueOf(rs.getString("specialite")),
                                rs.getDouble("salaire_heure")
                        );

                        artisans.add(artisan);
                    }
                }
            }
            return artisans;
        }
        private Equipe mapEquipe(ResultSet rs, Connection conn) throws SQLException {
            Equipe equipe = new Equipe();

            // Propriétés de base
            equipe.setId(rs.getInt("id"));
            equipe.setNom(rs.getString("nom"));
            equipe.setDateCreation(rs.getDate("date_creation").toLocalDate());

            // Chargement complet du constructeur
            int constructeurId = rs.getInt("constructeur_id");
            Constructeur constructeur = new UtilisateurDAO().getConstructeurId(constructeurId);
            equipe.setConstructeur(constructeur);

            // Chargement complet du gestionnaire de stock
            int gestionnaireId = rs.getInt("gestionnairestock_id");
            GestionnaireDeStock gestionnaire = new UtilisateurDAO().getGestionnaireStockId(gestionnaireId);
            equipe.setGestionnaireStock(gestionnaire);

            // Chargement des artisans

            equipe.setArtisans(getArtisansForEquipe(conn, equipe.getId()));

            return equipe;
        }


        public int getEquipeByName(String nomEquipe) {
            // Retrieve the equipe based on nom
            String sql = "SELECT * FROM Equipe WHERE nom = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nomEquipe);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving equipe by name: " + e.getMessage());
            }
            return -1;
        }

        public String getNomEquipeById(int id) {
            String nomEquipe = null;
            String query = "SELECT nom FROM Equipe WHERE id = ?";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        nomEquipe = rs.getString("nom");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return nomEquipe;
        }

        public List<Artisan> findAvailableArtisans(int equipeId) throws SQLException {
            String query = "SELECT a.artisan_id, u.id AS user_id, u.nom, u.prenom, u.email, u.telephone, u.adresse, u.mot_de_passe, u.statut, u.isConfirmed, u.role, a.specialite, a.salaire_heure " +
                    "FROM Artisan a " +
                    "JOIN Utilisateur u ON a.artisan_id = u.id " + // Jointure entre Artisan et Utilisateur
                    "LEFT JOIN Equipe_Artisan ea ON a.artisan_id = ea.artisan_id AND ea.equipe_id = ? " + // Jointure avec Equipe_Artisan
                    "WHERE ea.artisan_id IS NULL"; // Filtre pour les artisans non associés à l'équipe

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, equipeId);
                ResultSet rs = stmt.executeQuery();

                List<Artisan> artisans = new ArrayList<>();
                while (rs.next()) {
                    // Création de l'objet Utilisateur
                    Utilisateur utilisateur = new Utilisateur(
                            rs.getInt("user_id"), // id de l'utilisateur
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            Utilisateur.Statut.valueOf(rs.getString("statut")),
                            rs.getBoolean("isConfirmed"),
                            Utilisateur.Role.valueOf(rs.getString("role"))
                    );

                    // Création de l'objet Artisan
                    Artisan artisan = new Artisan(
                            rs.getInt("artisan_id"), // artisan_id de la table Artisan
                            utilisateur,
                            Artisan.Specialite.valueOf(rs.getString("specialite")),
                            rs.getDouble("salaire_heure")
                    );

                    artisans.add(artisan);
                }

                // Log pour vérifier les résultats
                return artisans;
            }
        }
        // insertion :

        public List<Constructeur> findAllConstructeurs() throws SQLException {
            String query = "SELECT a.constructeur_id, u.id AS user_id, u.nom, u.prenom, u.email, u.telephone, u.adresse, " +
                    "u.mot_de_passe, u.statut, u.isConfirmed, u.role, a.specialite, a.salaire_heure " +
                    "FROM Constructeur a " +
                    "JOIN Utilisateur u ON a.constructeur_id = u.id " +
                    "JOIN equipe e ON e.gestionnairestock_id = a.constructeur_id " +
                    "WHERE e.id = ?";

            List<Constructeur> constructeurs = new ArrayList<>();

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {


                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Création de l'objet Utilisateur
                    Utilisateur utilisateur = new Utilisateur(
                            rs.getInt("user_id"), // id de l'utilisateur
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            Utilisateur.Statut.valueOf(rs.getString("statut")),
                            rs.getBoolean("isConfirmed"),
                            Utilisateur.Role.valueOf(rs.getString("role"))

                    );

                    // Création de l'objet Constructeur
                    Constructeur constructeur = new Constructeur(
                            rs.getInt("constructeur_id"), // constructeur_id de la table Constructeur
                            utilisateur,
                            rs.getString("specialite"),
                            rs.getDouble("salaire_heure")
                    );

                    constructeurs.add(constructeur); // Ajout à la liste
                }
            }

            return constructeurs;  // Retourne la liste des constructeurs
        }

        public List<GestionnaireDeStock> findAllGestionnaires() throws SQLException {
            String query = "SELECT a.gestionnairestock_id, u.id AS user_id, u.nom, u.prenom, u.email, u.telephone, u.adresse, " +
                    "u.mot_de_passe, u.statut, u.isConfirmed, u.role" +
                    "FROM Gestionnairestock a " +
                    "JOIN Utilisateur u ON a.gestionnairestock_id = u.id ";


            List<GestionnaireDeStock> gestionnaires = new ArrayList<>();
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {


                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Création de l'objet Utilisateur
                    Utilisateur utilisateur = new Utilisateur(
                            rs.getInt("user_id"), // id de l'utilisateur
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("telephone"),
                            rs.getString("adresse"),
                            Utilisateur.Statut.valueOf(rs.getString("statut")),
                            rs.getBoolean("isConfirmed"),
                            Utilisateur.Role.valueOf(rs.getString("role"))


                    );

                    // Création de l'objet Constructeur
                    GestionnaireDeStock gestionnaireDeStock = new GestionnaireDeStock(
                            rs.getInt("gestionnairestock_id"),
                            utilisateur
                    );

                    gestionnaires.add(gestionnaireDeStock); // Ajout à la liste
                }
            }

            return gestionnaires;  // Retourne la liste des constructeurs
        }

        private void insertArtisan(Connection conn, int equipeId, List<Artisan> artisans) throws SQLException {
            String sql = "INSERT INTO Equipe_Artisan (equipe_id, artisan_id) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Artisan artisan : artisans) {
                    stmt.setInt(1, equipeId);
                    stmt.setInt(2, artisan.getArtisan_id());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        public void updateArtisans(int equipeId, List<Artisan> artisans) throws SQLException {
            try (Connection conn = connect()) {
                conn.setAutoCommit(false); // Début de la transaction

                try {
                    // Supprimer les anciennes associations
                    deleteArtisans(conn, equipeId);

                    // Insérer les nouvelles associations
                    insertArtisan(conn, equipeId, artisans);

                    conn.commit(); // Validation de la transaction
                } catch (SQLException e) {
                    conn.rollback(); // Annulation en cas d'erreur
                    throw new SQLException("Erreur lors de la mise à jour des artisans : " + e.getMessage(), e);
                }
            }
        }

        public List<Artisan> getAssignedArtisans(int equipeId) throws SQLException {
            String sql = "SELECT a.*, u.* FROM Artisan a " +
                    "JOIN Utilisateur u ON a.artisan_id = u.id " +
                    "JOIN Equipe_Artisan ea ON a.artisan_id = ea.artisan_id " +
                    "WHERE ea.equipe_id = ?";

            List<Artisan> artisans = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, equipeId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Utilisateur utilisateur = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("mot_de_passe"),
                                rs.getString("telephone"),
                                rs.getString("adresse"),
                                Utilisateur.Statut.valueOf(rs.getString("statut")),
                                rs.getBoolean("isConfirmed"),
                                Utilisateur.Role.valueOf(rs.getString("role"))

                        );

                        Artisan artisan = new Artisan(
                                rs.getInt("artisan_id"),
                                utilisateur,
                                Artisan.Specialite.valueOf(rs.getString("specialite")),
                                rs.getDouble("salaire_heure")
                        );

                        artisans.add(artisan);
                    }
                }
            }
            return artisans;
        }

        private int createConversation(Connection conn, Equipe equipe) throws SQLException {
            String sql = "INSERT INTO Conversation (equipe_id, nom) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, equipe.getId());
                stmt.setString(2, "Discussion - " + equipe.getNom());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            return -1;
        }

        private void addMembersToConversation(Connection conn, int conversationId, Equipe equipe) throws SQLException {
            String sql = "INSERT INTO Conversation_Membre (conversation_id, utilisateur_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Set<Integer> membres = new HashSet<>();

                // Récupération des IDs utilisateur
                membres.add(getUserIdByRoleId(equipe.getConstructeur().getConstructeur_id()));
                membres.add(getUserIdByRoleId(equipe.getGestionnaireStock().getGestionnairestock_id()));
                for (Artisan a : equipe.getArtisans()) {
                    membres.add(getUserIdByRoleId(a.getArtisan_id()));
                }

                for (Integer userId : membres) {
                    stmt.setInt(1, conversationId);
                    stmt.setInt(2, userId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        private void sendConversationNotifications(Connection conn, Equipe equipe, int conversationId) throws SQLException {
            String message = "Vous avez été ajouté à la conversation de l'équipe " + equipe.getNom();

            Set<Integer> membres = new HashSet<>();
            membres.add(getUserIdByRoleId(equipe.getConstructeur().getConstructeur_id()));
            membres.add(getUserIdByRoleId(equipe.getGestionnaireStock().getGestionnairestock_id()));
            for (Artisan a : equipe.getArtisans()) {
                membres.add(getUserIdByRoleId(a.getArtisan_id()));
            }

            for (Integer userId : membres) {
                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.createNotifications(new Notification(
                        userId,
                        message,
                        "CONVERSATION",
                        conversationId
                ));
            }
        }
        public int getUserIdByRoleId(int roleSpecificId) throws SQLException {
            String sql = "SELECT user_id FROM messaging_accounts WHERE role_specific_id = ?";

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, roleSpecificId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("Aucun utilisateur trouvé pour le roleSpecificId: " + roleSpecificId);
                }
            }
        }
        public List<Equipe> getTeamsByUserId(int userId) throws SQLException {
            String sql = """
        SELECT e.* 
        FROM equipe e
        LEFT JOIN constructeur c ON e.constructeur_id = c.constructeur_id
        LEFT JOIN utilisateur u1 ON c.constructeur_id = u1.id
        LEFT JOIN gestionnairestock g ON e.gestionnairestock_id = g.gestionnairestock_id
        LEFT JOIN utilisateur u2 ON g.gestionnairestock_id = u2.id
        LEFT JOIN equipe_artisan ea ON e.id = ea.equipe_id
        LEFT JOIN artisan a ON ea.artisan_id = a.artisan_id
        LEFT JOIN utilisateur u3 ON a.artisan_id = u3.id
        WHERE u1.id = ? OR u2.id = ? OR u3.id = ?
        """;

            List<Equipe> equipes = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.setInt(3, userId);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Equipe equipe = new Equipe();
                    equipe.setId(rs.getInt("id"));
                    equipe.setNom(rs.getString("nom"));

                    // Chargement du constructeur
                    int constructeurId = rs.getInt("constructeur_id");
                    Constructeur constructeur = new UtilisateurDAO().getConstructeurId(constructeurId);
                    equipe.setConstructeur(constructeur);

                    // Chargement du gestionnaire de stock
                    int gestionnaireId = rs.getInt("gestionnairestock_id");
                    GestionnaireDeStock gestionnaire = new UtilisateurDAO().getGestionnaireStockId(gestionnaireId);
                    equipe.setGestionnaireStock(gestionnaire);

                    // Chargement des artisans
                    equipe.setArtisans(getArtisansForEquipe(conn, equipe.getId()));

                    equipes.add(equipe);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des équipes : " + e.getMessage());
                throw e;
            }
            return equipes;
        }
        public List<Utilisateur> getTeamMembers(int teamId) throws SQLException {
            String sql = """
        SELECT u.* 
        FROM utilisateur u
        WHERE u.id IN (
            SELECT u1.id FROM constructeur c
            JOIN utilisateur u1 ON c.constructeur_id = u1.id
            WHERE c.constructeur_id = (SELECT e.constructeur_id FROM equipe e WHERE e.id = ?)
            
            UNION
            
            SELECT u2.id FROM gestionnairestock gs
            JOIN utilisateur u2 ON gs.gestionnairestock_id = u2.id
            WHERE gs.gestionnairestock_id = (SELECT e.gestionnairestock_id FROM equipe e WHERE e.id = ?)
            
            UNION
            
            SELECT u3.id FROM equipe_artisan ea
            JOIN artisan a ON ea.artisan_id = a.artisan_id
            JOIN utilisateur u3 ON a.artisan_id = u3.id
            WHERE ea.equipe_id = ?
        )
        """;

            List<Utilisateur> members = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, teamId); // constructeur_id de l'équipe
                stmt.setInt(2, teamId); // gestionnairestock_id de l'équipe
                stmt.setInt(3, teamId); // artisans liés à l'équipe

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Utilisateur user = new Utilisateur();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    // Ajouter les autres propriétés nécessaires
                    members.add(user);
                }
            }
            return members;
        }

        public List<Utilisateur> getUsersByRole(Utilisateur.Role role) {
            List<Utilisateur> users = new ArrayList<>();
            String sql = "SELECT * FROM Utilisateur WHERE role = ?";

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, role.name());
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Utilisateur user = UtilisateurDAO.mapUtilisateur(rs);

                    // Forcer le chargement des sous-entités si nécessaire
                    switch(role) {
                        case Artisan:
                            user.setArtisan(UtilisateurDAO.getArtisanByUserId(user.getId()));
                            break;
                        case Constructeur:
                            user.setConstructeur(UtilisateurDAO.getConstructeurByUserId(user.getId()));
                            break;
                    }

                    users.add(user);
                }
            } catch (SQLException e) {
                UtilisateurDAO.handleException("Erreur de récupération par rôle", e);
            }
            return users;
        }
    }