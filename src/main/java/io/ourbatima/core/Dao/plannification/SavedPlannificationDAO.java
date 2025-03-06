package io.ourbatima.core.Dao.plannification;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.controllers.SessionManager;
import io.ourbatima.core.model.Utilisateur.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SavedPlannificationDAO {

    public boolean savePlannification(int plannificationId) {
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) {
            System.err.println("Aucun utilisateur connecté !");
            return false;
        }

        String sql = "INSERT INTO SavedPlannification (user_id, plannification_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id=user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, plannificationId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlannificationSaved(int plannificationId) {
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) return false;

        String sql = "SELECT COUNT(*) FROM SavedPlannification WHERE user_id = ? AND plannification_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, plannificationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
