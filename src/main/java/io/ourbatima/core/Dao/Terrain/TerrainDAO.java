package io.ourbatima.core.Dao.Terrain;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Terrain;
import io.ourbatima.core.model.Visite;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainDAO {


    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }


    public void addTerrain(Terrain terrain) {
        String sql = "INSERT INTO Terrain (emplacement, caracteristiques, superficie, detailsGeo) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, terrain.getEmplacement());
            pstmt.setString(2, terrain.getCaracteristiques());

            if (terrain.getSuperficie() != null) {
                pstmt.setBigDecimal(3, terrain.getSuperficie());
            } else {
                pstmt.setNull(3, Types.DECIMAL);
            }

            pstmt.setString(4, terrain.getDetailsGeo());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        terrain.setId_terrain(generatedKeys.getInt(1));
                        System.out.println("Terrain ajouté avec succès ! Id_terrain = " + terrain.getId_terrain());
                    }
                }
            } else {
                System.out.println("Échec de l'ajout du terrain, aucune ligne affectée.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du terrain : " + e.getMessage());
        }
    }




    public List<Terrain> getAllTerrain() {
        List<Terrain> terrains = new ArrayList<>();
        String sql = "SELECT Id_terrain, detailsGeo, superficie, caracteristiques, emplacement FROM Terrain";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idTerrain = rs.getInt("Id_terrain");
                BigDecimal superficie = rs.getObject("superficie") != null ? rs.getBigDecimal("superficie") : null;
                String detailsGeo = rs.getString("detailsGeo");
                String caracteristiques = rs.getString("caracteristiques");
                String emplacement = rs.getString("emplacement");

                // Create Terrain object
                Terrain terrain = new Terrain(idTerrain, detailsGeo, superficie, caracteristiques, emplacement);



                // Fetch visits for this terrain to get the observations
                List<String> observations = getObservationsForTerrain(idTerrain);
                terrain.setObservations(observations);
                terrains.add(terrain);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des terrains : " + e.getMessage());
        }
        return terrains;
    }

    public List<String> getAllEmplacements() {
        List<String> emplacements = new ArrayList<>();
        String query = "SELECT emplacement FROM Terrain";

        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                emplacements.add(resultSet.getString("emplacement"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching emplacements: " + e.getMessage());
        }

        return emplacements;
    }




    public List<String> getObservationsForTerrain(int idTerrain) {
        List<String> observations = new ArrayList<>();
        String sql = "SELECT observations FROM Visite WHERE IdTerrain = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTerrain);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    observations.add(rs.getString("observations"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des observations pour le terrain " + idTerrain + ": " + e.getMessage());
        }
        return observations;
    }




    public Terrain getTerrainById(int Id_terrain) {
        String sql = "SELECT * FROM Terrain WHERE Id_terrain = ?";
        Terrain terrain = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Id_terrain);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal superficie = rs.getObject("superficie") != null ? rs.getBigDecimal("superficie") : null;

                    terrain = new Terrain(
                            rs.getInt("Id_terrain"),
                            rs.getString("detailsGeo"),
                            superficie,
                            rs.getString("caracteristiques"),
                            rs.getString("emplacement")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du terrain par ID : " + e.getMessage());
        }
        return terrain;
    }



    public void updateTerrain(Terrain terrain) {
        String sql = "UPDATE Terrain SET emplacement = ?, caracteristiques = ?, superficie = ?, detailsGeo = ? WHERE Id_terrain = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, terrain.getEmplacement());
            pstmt.setString(2, terrain.getCaracteristiques());

            if (terrain.getSuperficie() != null) {
                pstmt.setBigDecimal(3, terrain.getSuperficie());
            } else {
                pstmt.setNull(3, Types.DECIMAL);
            }
            pstmt.setString(4, terrain.getDetailsGeo());
            pstmt.setInt(5, terrain.getId_terrain());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Terrain mis à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du terrain : " + e.getMessage());
        }
    }

    public void updateVisiteObservations(int idVisite, String observations) {
        String sql = "UPDATE Visite SET Observations = ? WHERE Id_visite = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, observations);
            pstmt.setInt(2, idVisite);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Observations updated successfully!");
            } else {
                System.out.println("No rows affected, check if the Id_visite exists.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating observations: " + e.getMessage());
        }
    }


    public Terrain getTerrainByEmplacement(String emplacement) {
        Terrain terrain = null;
        String sql = "SELECT * FROM Terrain WHERE emplacement = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emplacement);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    terrain = new Terrain(
                            rs.getInt("Id_terrain"),
                            rs.getString("detailsGeo"),
                            rs.getBigDecimal("superficie"),
                            rs.getString("caracteristiques"),
                            rs.getString("emplacement")
                    );
                } else {
                    System.out.println("No Terrain found with emplacement: " + emplacement);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du terrain : " + e.getMessage());
        }
        return terrain;
    }


    public void deleteTerrain(int idTerrain) {
        String sql = "DELETE FROM Terrain WHERE Id_terrain = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTerrain);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Terrain supprimé avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du terrain : " + e.getMessage());
        }
    }


    public static String getTerrainEmplacementById(int id) {
        String sql = "SELECT emplacement FROM Terrain WHERE Id_terrain = ?";
        String emplacement = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    emplacement = rs.getString("emplacement");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emplacement;
    }

    public int getIdTerrainByEmplacement(String emplacement) {
        String sql = "SELECT Id_terrain FROM Terrain WHERE emplacement = ?";
        int idTerrain = -1; // Default value indicating not found

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emplacement);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idTerrain = rs.getInt("Id_terrain"); // Get the Id_terrain from the result set
                } else {
                    System.out.println("❌ No Terrain found with emplacement: " + emplacement);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'Id_terrain par emplacement : " + e.getMessage());
        }
        return idTerrain;
    }

    public Terrain getLastInsertedTerrain() {
        String sql = "SELECT emplacement, superficie FROM Terrain ORDER BY id_terrain DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String emplacement = rs.getString("emplacement");
                BigDecimal superficie = rs.getBigDecimal("superficie");
                return new Terrain(emplacement, superficie);  // Assuming your Terrain class has this constructor
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}