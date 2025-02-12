package io.OurBatima.core.Dao.taches;

import io.OurBatima.core.model.Tache;
import io.OurBatima.core.Dao.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {

    public void ajouterTache(Tache tache) {
        String sql = "INSERT INTO tache (Id_projet, constructeur_id, artisan_id, description, date_debut, date_fin, etat) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tache.getIdProjet());
            stmt.setObject(2, tache.getConstructeurId(), Types.INTEGER);
            stmt.setObject(3, tache.getArtisanId(), Types.INTEGER);
            stmt.setString(4, tache.getDescription());
            stmt.setDate(5, tache.getDateDebut());
            stmt.setDate(6, tache.getDateFin());
            stmt.setString(7, tache.getEtat());

            stmt.executeUpdate();
            System.out.println("Tâche ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tache> getAllTaches() {
        List<Tache> taches = new ArrayList<>();
        String sql = "SELECT * FROM tache";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tache tache = new Tache(
                        rs.getInt("id_tache"),
                        rs.getInt("Id_projet"),
                        (Integer) rs.getObject("constructeur_id"),
                        (Integer) rs.getObject("artisan_id"),
                        rs.getString("description"),
                        rs.getDate("date_debut"),
                        rs.getDate("date_fin"),
                        rs.getString("etat")
                );
                taches.add(tache);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taches;
    }

    public void supprimerTache(int idTache) {
        String sql = "DELETE FROM tache WHERE id_tache = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTache);
            stmt.executeUpdate();
            System.out.println("Tâche supprimée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierTache(Tache tache) {
        String sql = "UPDATE tache SET Id_projet = ?, constructeur_id = ?, artisan_id = ?, description = ?, date_debut = ?, date_fin = ?, etat = ? WHERE id_tache = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tache.getIdProjet());
            stmt.setObject(2, tache.getConstructeurId(), Types.INTEGER);
            stmt.setObject(3, tache.getArtisanId(), Types.INTEGER);
            stmt.setString(4, tache.getDescription());
            stmt.setDate(5, tache.getDateDebut());
            stmt.setDate(6, tache.getDateFin());
            stmt.setString(7, tache.getEtat());
            stmt.setInt(8, tache.getIdTache());

            stmt.executeUpdate();
            System.out.println("Tâche mise à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}