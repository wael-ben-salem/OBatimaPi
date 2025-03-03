    package io.ourbatima.core.Dao.Utilisateur;

    import io.ourbatima.controllers.MessagingService;
    import io.ourbatima.core.Dao.DatabaseConnection;
    import io.ourbatima.core.model.Utilisateur.*;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.sql.Connection;
    import java.sql.SQLException;

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
                new MessagingService().sendTeamNotification(equipe,
                        "Vous avez été ajouté à l'équipe " + equipe.getNom() +
                                " - Date de création : " + equipe.getDateCreation());

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
                        equipe = mapEquipe(rs,conn);
                        equipe.setArtisans(getArtisansForEquipe(conn, id));
                    }
                }
            } catch (SQLException e) {
                throw new SQLException("Erreur recherche équipe: " + e.getMessage(), e);
            }
            return equipe;
        }

        private List<Artisan> getArtisansForEquipe(Connection conn, int equipeId) throws SQLException {
            String sql = "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, u.telephone, u.adresse, u.role, " +
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
                    "JOIN Utilisateur u ON a.gestionnairestock_id = u.id " ;


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








    }
