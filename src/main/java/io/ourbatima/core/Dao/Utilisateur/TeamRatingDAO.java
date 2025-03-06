package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VoteDAO {
    public boolean hasAlreadyVoted(int userId, int equipeId) throws SQLException {
        String sql = "SELECT 1 FROM Votes WHERE user_id = ? AND equipe_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, equipeId);
            return stmt.executeQuery().next();
        }
    }

    public void saveVote(int userId, int equipeId, int rating) throws SQLException {
        String sql = "INSERT INTO Votes (user_id, equipe_id, rating) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, equipeId);
            stmt.setInt(3, rating);
            stmt.executeUpdate();
        }
    }
}