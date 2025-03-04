package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.DirectMessage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DirectMessageDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }


    // Créer un nouveau message
    public void createMessage(DirectMessage message) throws SQLException {
        String sql = "INSERT INTO direct_messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, message.getSenderId());
            pstmt.setInt(2, message.getReceiverId());
            pstmt.setString(3, message.getContent());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setMessageId(rs.getInt(1));
                }
            }
        }
    }

    // Récupérer les messages entre deux utilisateurs
    public List<DirectMessage> getConversation(int user1, int user2) throws SQLException {
        String sql = "SELECT * FROM direct_messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY sent_at ASC";

        List<DirectMessage> messages = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user1);
            pstmt.setInt(2, user2);
            pstmt.setInt(3, user2);
            pstmt.setInt(4, user1);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }
        }
        return messages;
    }

    // Marquer un message comme lu
    public void markAsRead(int messageId) throws SQLException {
        String sql = "UPDATE direct_messages SET is_read = true WHERE message_id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, messageId);
            pstmt.executeUpdate();
        }
    }

    // Mapper un ResultSet vers un DirectMessage
    private DirectMessage mapMessage(ResultSet rs) throws SQLException {
        return new DirectMessage(
                rs.getInt("message_id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getString("content"),
                rs.getTimestamp("sent_at").toLocalDateTime(),
                rs.getBoolean("is_read")
        );
    }
}