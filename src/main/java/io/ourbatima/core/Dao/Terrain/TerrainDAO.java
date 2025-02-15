package io.ourbatima.core.Dao.Terrain;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Terrain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainDAO {

    // Method to establish a database connection
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    //Add a new Terrain
    public void addTerrain(Terrain terrain) {
        String sql = "INSERT INTO Terrain (Id_projet, emplacement, caracteristiques, superficie, detailsGeo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, terrain.getId_projet());
            pstmt.setString(2, terrain.getEmplacement());
            pstmt.setString(3, terrain.getCaracteristiques());
            pstmt.setBigDecimal(4, terrain.getSuperficie());  // Use setBigDecimal for superficie
            pstmt.setString(5, terrain.getDetailsGeo());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        terrain.setId_terrain(generatedKeys.getInt(1));  // Set the generated ID
                        System.out.println("Terrain ajouté avec succès ! Id_terrain = " + terrain.getId_terrain());
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du terrain : " + e.getMessage());
        }
    }


    //Get all Terrain
    public List<Terrain> getAllTerrain() {
        List<Terrain> terrains = new ArrayList<>();
        String sql = "SELECT * FROM Terrain";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Terrain terrain = new Terrain(
                        rs.getInt("Id_terrain"),
                        rs.getInt("Id_projet"),
                        rs.getString("detailsGeo"),
                        rs.getBigDecimal("superficie"),
                        rs.getString("caracteristiques"),
                        rs.getString("emplacement")
                );
                terrains.add(terrain);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des terrains : " + e.getMessage());
        }
        return terrains;
    }


    //Get a Terrain by ID
    public Terrain getTerrainById(int idTerrain) {
        String sql = "SELECT * FROM Terrain WHERE Id_terrain = ?";
        Terrain terrain = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTerrain);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    terrain = new Terrain(
                            rs.getInt("Id_terrain"),
                            rs.getInt("Id_projet"),
                            rs.getString("detailsGeo"),
                            rs.getBigDecimal("superficie"),
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

    //Update a Terrain
    public void updateTerrain(Terrain terrain) {
        String sql = "UPDATE Terrain SET Id_projet = ?, emplacement = ?, caracteristiques = ?, superficie = ?, detailsGeo = ? WHERE Id_terrain = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, terrain.getId_projet());
            pstmt.setString(2, terrain.getEmplacement());
            pstmt.setString(3, terrain.getCaracteristiques());
            pstmt.setBigDecimal(4, terrain.getSuperficie());
            pstmt.setString(5, terrain.getDetailsGeo());
            pstmt.setInt(6, terrain.getId_terrain());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Terrain mis à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du terrain : " + e.getMessage());
        }
    }

    //Delete a Terrain
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
}
