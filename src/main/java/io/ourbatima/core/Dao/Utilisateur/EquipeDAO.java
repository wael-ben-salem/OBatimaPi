    package io.ourbatima.core.Dao.Utilisateur;

    import io.ourbatima.core.Dao.DatabaseConnection;
    import io.ourbatima.core.model.Utilisateur.*;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.sql.Connection;
    import java.sql.SQLException;

    public class EquipeDAO {
        // Méthode de connexion
        private Connection connect() throws SQLException {
            return DatabaseConnection.getConnection();
        }

        public void create(Equipe equipe) throws SQLException {
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

            } catch (SQLException e) {
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
            String sql = "SELECT u.id, u.nom, u.prenom, u.email, u.telephone, u.adresse, u.role, " +
                    "a.artisan_id, a.specialite " + // Ajoutez d'autres colonnes spécifiques à Artisan si nécessaire
                    "FROM utilisateur u " +
                    "JOIN Artisan a ON u.id = a.artisan_id " +
                    "JOIN Equipe_Artisan ea ON a.artisan_id = ea.artisan_id " +
                    "WHERE ea.equipe_id = ?";

            List<Artisan> artisans = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, equipeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Création de l'utilisateur parent
                        Utilisateur utilisateur = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("email"),
                                rs.getString("telephone"),
                                rs.getString("adresse"),
                                Utilisateur.Statut.actif, // Vous pouvez ajuster cela selon votre logique
                                true, // isConfirmed
                                Utilisateur.Role.valueOf(rs.getString("role")) // Rôle
                        );

                        // Création de l'artisan avec l'utilisateur parent
                        Artisan artisan = new Artisan(
                                rs.getInt("artisan_id"),
                                utilisateur // Associez l'utilisateur parent
                        );

                        // Ajoutez d'autres propriétés spécifiques à Artisan si nécessaire
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
    }


    public int getEquipeByName(String nomEquipe) {
        // Retrieve the equipe based on nom
        String sql = "SELECT * FROM Equipe WHERE nom = ?";
        try (Connection conn = DatabaseConnection.getConnection();
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



}
