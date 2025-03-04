package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.Utilisateur.DirectMessageDAO;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.NotificationDAO;
import io.ourbatima.core.model.Utilisateur.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagingService {

    private final EquipeDAO equipeDAO = new EquipeDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final DirectMessageDAO messageDao = new DirectMessageDAO();


    public void createUserMessagingAccount(Utilisateur user, int roleSpecificId) {
        try (Connection messagingConn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO messaging_accounts (user_id, username, role_specific_id) VALUES (?, ?, ?)";
            PreparedStatement pstmt = messagingConn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, roleSpecificId); // Ajout de l'ID spécifique au rôle
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Messaging service error: " + e.getMessage());
        }
    }
    public void sendTeamNotification(Equipe equipe, String message) throws SQLException {
        List<Integer> memberIds = new ArrayList<>();

        // Ajouter le constructeur
        memberIds.add(equipe.getConstructeur().getConstructeur_id());

        // Ajouter le gestionnaire de stock
        memberIds.add(equipe.getGestionnaireStock().getGestionnairestock_id());

        // Ajouter tous les artisans
        for (Artisan artisan : equipe.getArtisans()) {
            memberIds.add(artisan.getArtisan_id());
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                for (int memberId : memberIds) {
                    Notification notification = new Notification(
                            getUserIdByRoleId(memberId, conn),
                            message
                    );
                    notificationDAO.createNotification(notification);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private int getUserIdByRoleId(int roleSpecificId, Connection conn) throws SQLException {
        String sql = "SELECT user_id FROM messaging_accounts WHERE role_specific_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleSpecificId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
            throw new SQLException("User non trouvé pour le roleSpecificId: " + roleSpecificId);
        }
    }
    public List<Notification> getUserNotifications(int userId) throws SQLException {
        return notificationDAO.getNotificationsByUser(userId);
    }
    // Nouvelle méthode pour les messages directs
    public void sendDirectMessage(int senderId, int receiverId, String message) throws SQLException {
        String sql = "INSERT INTO direct_messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        }
    }

    // Récupération des messages directs
    public List<DirectMessage> getDirectMessages(int userId) throws SQLException {
        List<DirectMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM direct_messages WHERE receiver_id = ? ORDER BY sent_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new DirectMessage(
                        rs.getInt("message_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("content"),
                        rs.getTimestamp("sent_at").toLocalDateTime(),
                        rs.getBoolean("is_read")
                ));
            }
        }
        return messages;
    }

    public List<Notification> getUserNotificationsSince(int userId, LocalDateTime since) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND created_at > ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, Timestamp.valueOf(since));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("read_at") != null ? rs.getTimestamp("read_at").toLocalDateTime() : null
                    ));
                }
            }
        }
        return notifications;
    }
    public void sendMessage(int senderId, int receiverId, String content) throws SQLException {
        DirectMessage message = new DirectMessage(senderId, receiverId, content);
        messageDao.createMessage(message);
    }

    public List<DirectMessage> getConversation(int user1, int user2) throws SQLException {
        return messageDao.getConversation(user1, user2);
    }

    public void markMessageAsRead(int messageId) throws SQLException {
        messageDao.markAsRead(messageId);
    }


}