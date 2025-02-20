package io.ourbatima.core.Dao.Terrain;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Terrain;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainDAO {


    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void addTerrain(Terrain terrain) {
        String sql = "INSERT INTO Terrain (Id_projet, emplacement, caracteristiques, superficie, detailsGeo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (terrain.getId_projet() != null) {
                pstmt.setInt(1, terrain.getId_projet());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, terrain.getEmplacement());
            pstmt.setString(3, terrain.getCaracteristiques());

            if (terrain.getSuperficie() != null) {
                pstmt.setBigDecimal(4, terrain.getSuperficie());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }

            pstmt.setString(5, terrain.getDetailsGeo());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        terrain.setId_terrain(generatedKeys.getInt(1));
                        System.out.println("Terrain ajouté avec succès ! Id_terrain = " + terrain.getId_terrain());
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du terrain : " + e.getMessage());
        }
    }


    public List<Terrain> getAllTerrain() {
        List<Terrain> terrains = new ArrayList<>();
        String sql = "SELECT * FROM Terrain";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Retrieve values first
                Integer idVisite = rs.getObject("Id_visite") != null ? rs.getInt("Id_visite") : null;
                BigDecimal superficie = rs.getObject("superficie") != null ? rs.getBigDecimal("superficie") : null;
                Integer idProjet = rs.getObject("Id_projet") != null ? rs.getInt("Id_projet") : null;


                Terrain terrain = new Terrain(
                        rs.getInt("Id_terrain"),
                        idProjet,
                        rs.getString("detailsGeo"),
                        superficie,
                        rs.getString("caracteristiques"),
                        rs.getString("emplacement"),
                        idVisite
                );
                terrains.add(terrain);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des terrains : " + e.getMessage());
        }
        return terrains;
    }

    public Terrain getTerrainById(int Id_terrain) {
        String sql = "SELECT * FROM Terrain WHERE Id_terrain = ?";
        Terrain terrain = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Id_terrain);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Integer idProjet = rs.getObject("Id_projet") != null ? rs.getInt("Id_projet") : null;
                    Integer idVisite = rs.getObject("Id_visite") != null ? rs.getInt("Id_visite") : null;
                    BigDecimal superficie = rs.getObject("superficie") != null ? rs.getBigDecimal("superficie") : null;

                    terrain = new Terrain(
                            rs.getInt("Id_terrain"),
                            idProjet,
                            rs.getString("detailsGeo"),
                            superficie,
                            rs.getString("caracteristiques"),
                            rs.getString("emplacement"),
                            idVisite
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du terrain par ID : " + e.getMessage());
        }
        return terrain;
    }


    public void updateTerrain(Terrain terrain) {
        String sql = "UPDATE Terrain SET Id_projet = ?, emplacement = ?, caracteristiques = ?, superficie = ?, detailsGeo = ?, Id_visite = ? WHERE Id_terrain = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (terrain.getId_projet() != null) {
                pstmt.setInt(1, terrain.getId_projet());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, terrain.getEmplacement());
            pstmt.setString(3, terrain.getCaracteristiques());

            if (terrain.getSuperficie() != null) {
                pstmt.setBigDecimal(4, terrain.getSuperficie());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }
            pstmt.setString(5, terrain.getDetailsGeo());
            pstmt.setObject(6, terrain.getId_visite());
            pstmt.setInt(7, terrain.getId_terrain());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Terrain mis à jour avec succès !");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du terrain : " + e.getMessage());
        }
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

    public Terrain getTerrainByEmplacement(String emplacement) {
        String sql = "SELECT * FROM Terrain WHERE emplacement = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emplacement);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Terrain(rs.getInt("Id_terrain"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving terrain by emplacement: " + e.getMessage());
        }
        return null;
    }
    public static String getTerrainEmplacementById(int id) {
        String sql = "SELECT emplacement FROM Terrain WHERE id_terrain = ?";
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


}
