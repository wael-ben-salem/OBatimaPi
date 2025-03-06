package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // Correction de la récupération de l'ID généré
    public void createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message, created_at) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getMessage());
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Utiliser le temps actuel

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    notification.setId(rs.getInt(1)); // Récupérer notification_id
                }
            }
        }
    }
    public List<Notification> getNotificationsByUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapNotification(rs));
            }
        }
        return notifications;
    }

    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true, read_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("message"),
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("read_at") != null ? rs.getTimestamp("read_at").toLocalDateTime() : null
        );
    }

    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true, read_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    public void createNotifications(Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (user_id, message, type, reference_id, is_read, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getMessage());
            pstmt.setString(3, notification.getType());
            pstmt.setInt(4, notification.getReferenceId());
            pstmt.setBoolean(5, notification.isRead());
            pstmt.setTimestamp(6, Timestamp.valueOf(notification.getCreatedAt()));

            pstmt.executeUpdate();
        }
    }
    public List<Notification> getUnreadNotificationsByType(int userId, String type) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false AND type = ? ORDER BY created_at DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        }
        return notifications;
    }
}