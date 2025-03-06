package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GroupDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void createGroup(int teamId, String groupName) throws SQLException {
        String sql = "INSERT INTO group_messages (group_id, sender_id, content) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Création du message système
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, 0); // ID 0 pour les messages système
            pstmt.setString(3, "Groupe créé ! Commencez à discuter.");
            pstmt.executeUpdate();
        }
    }
}