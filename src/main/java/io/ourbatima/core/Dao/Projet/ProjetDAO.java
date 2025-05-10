package io.ourbatima.core.Dao.Projet;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Utilisateur.Client;
import io.ourbatima.core.model.Utilisateur.Utilisateur;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Add a new project
    public int addProjet(Projet projet) {
        if (projet.getEtat() == null || projet.getEtat().isEmpty()) {
            projet.setEtat("En attente");
        }
        String sql = "INSERT INTO Projet (nomProjet, Id_equipe, id_client, Id_terrain, budget, type, styleArch, etat, dateCreation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, projet.getNomProjet());
            pstmt.setObject(2, projet.getId_equipe(), java.sql.Types.INTEGER);
            pstmt.setInt(3, projet.getId_client());
            pstmt.setInt(4, projet.getId_terrain());
            pstmt.setBigDecimal(5, projet.getBudget());
            pstmt.setString(6, projet.getType());
            pstmt.setString(7, projet.getStyleArch());
            pstmt.setString(8, projet.getEtat());
            pstmt.setTimestamp(9, projet.getDateCreation());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion du projet : " + e.getMessage());
        }
        return -1;
    }


    public List<EtapeProjet> getEtapesForProjet(int Id_projet) {
        List<EtapeProjet> etapes = new ArrayList<>();
        String sql = "SELECT nomEtape FROM EtapeProjet WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Id_projet);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    etapes.add(new EtapeProjet(rs.getString("nomEtape")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étapes pour l'ID du projet " + Id_projet + ": " + e.getMessage());
        }
        return etapes;
    }


    public Terrain getTerrainForProjet(int Id_projet) {
        String sql = "SELECT emplacement FROM Terrain WHERE Id_projet = ?";
        Terrain terrain = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Id_projet);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    terrain = new Terrain(rs.getString("emplacement"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du terrain pour l'ID du projet " + Id_projet + ": " + e.getMessage());
        }
        return terrain;
    }

    public List<Projet> getAllProjets() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM Projet";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int Id_projet = rs.getInt("Id_projet");
                String nomProjet = rs.getString("nomProjet");
                Integer Id_equipe = rs.getObject("Id_equipe", Integer.class);
                int id_client = rs.getInt("id_client");
                int id_terrain = rs.getInt("Id_terrain");
                String type = rs.getString("type");
                String styleArch = rs.getString("styleArch");
                BigDecimal budget = rs.getBigDecimal("budget");
                String etat = rs.getString("etat");
                Timestamp dateCreation = rs.getTimestamp("dateCreation");

                UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                Utilisateur utilisateur = utilisateurDAO.getUserById(id_client, conn);

                String emailClient = Optional.ofNullable(utilisateur)
                        .filter(u -> u instanceof Client)
                        .map(u -> ((Client) u).getEmail())
                        .orElse("Aucun client attribué.");

                // Retrieve the list of EtapeProjet for this project
                List<EtapeProjet> etapes = getEtapesForProjet(Id_projet);
                Projet projet = new Projet(Id_projet, nomProjet, type, styleArch, budget, etat, dateCreation, id_terrain, Id_equipe, id_client, etapes);
                projet.setEmailClient(emailClient);
                projets.add(projet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }




    public Projet getProjetById(int Id_projet) {
        String sql = "SELECT * FROM Projet WHERE Id_projet = ?";
        Projet projet = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Id_projet);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer idEquipe = rs.getObject("Id_equipe") != null ? rs.getInt("Id_equipe") : null;
                    int idClient = rs.getInt("id_client");
                    String nomProjet = rs.getString("nomProjet");
                    BigDecimal budget = rs.getBigDecimal("budget");
                    String type = rs.getString("type");
                    String styleArch = rs.getString("styleArch");
                    String etat = rs.getString("etat");
                    Timestamp dateCreation = rs.getTimestamp("dateCreation");

                    List<EtapeProjet> etapes = getEtapesForProjet(Id_projet);
                    Terrain terrain = getTerrainForProjet(Id_projet);

                    projet = new Projet(Id_projet, nomProjet, idEquipe, idClient, etapes, budget, terrain, type, styleArch, etat, dateCreation);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du projet par ID : " + e.getMessage());
        }
        return projet;
    }


    public void updateProjet(Projet projet) {
        String sql = "UPDATE Projet SET Id_equipe = ?, id_client = ?, nomProjet = ?, type = ?, styleArch = ?, budget = ?, etat = ? WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (projet.getId_equipe() != null) {
                pstmt.setInt(1, projet.getId_equipe());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setInt(2, projet.getId_client());
            pstmt.setString(3, projet.getNomProjet());
            pstmt.setString(4, projet.getType());
            pstmt.setString(5, projet.getStyleArch());
            pstmt.setBigDecimal(6, projet.getBudget());
            pstmt.setString(7, projet.getEtat());
            pstmt.setInt(8, projet.getId_projet());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projet mis à jour avec succès !");
            } else {
                System.out.println("Aucun projet trouvé avec l'ID " + projet.getId_projet());
            }

            EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();
            for (EtapeProjet etape : projet.getEtapes()) {
                etapeProjetDAO.updateEtapeProjet(etape);
            }

            Terrain terrainToUpdate = projet.getTerrain();
            if (terrainToUpdate != null) {
                terrainToUpdate.setId_terrain(projet.getId_terrain());
                TerrainDAO terrainDAO = new TerrainDAO();
                terrainDAO.updateTerrain(terrainToUpdate);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du projet : " + e.getMessage());
        }
    }


    public void deleteProjet(int Id_projet) {
        String sql = "DELETE FROM Projet WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Id_projet);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projet supprimé avec succès !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du projet : " + e.getMessage());
        }
    }


    public void assignEquipeToProjet(int Id_projet, int equipeId) {
        String sql = "UPDATE Projet SET Id_equipe = ? WHERE Id_projet = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipeId);
            pstmt.setInt(2, Id_projet);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Équipe " + equipeId + " assignée avec succès au projet " + Id_projet);
            } else {
                System.out.println("Projet avec l'ID " + Id_projet + " non trouvé.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'assignation de l'équipe au projet : " + e.getMessage());
        }
    }

    public int getIdByNom(String nomProjet) {
        String sql = "SELECT Id_projet FROM Projet WHERE nomProjet = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomProjet);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id_projet");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID du projet par nom : " + e.getMessage());
        }
        return -1;
    }

    public List<String> getAllProjetNames() {
        List<String> projectNames = new ArrayList<>();
        String query = "SELECT nomProjet FROM Projet";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projectNames.add(rs.getString("nomProjet"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Projects retrieved: " + projectNames); // Debugging line
        return projectNames;
    }

    public List<String> getProjectTypes() {
        List<String> types = new ArrayList<>();
        String query = "SELECT DISTINCT type FROM Projet";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                types.add(resultSet.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public Projet getLastInsertedProjet() {
        String sql = "SELECT type, styleArch FROM Projet ORDER BY id_projet DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String type = rs.getString("type");
                String styleArch = rs.getString("styleArch");
                return new Projet(type, styleArch);  // Assuming your Projet class has this constructor
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}