package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.Utilisateur.DirectMessageDAO;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.NotificationDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
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
    public void sendTeamNotifications(Equipe equipe, String message, String notificationType, int referenceId) throws SQLException {        List<Integer> memberIds = new ArrayList<>();

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
                            message,
                            notificationType,
                            referenceId
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
    public void sendTeamNotification(Equipe equipe, String message) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Notification pour le constructeur
                Utilisateur constructeur = getUserByRoleAndId(
                        equipe.getConstructeur().getConstructeur_id(),
                        Utilisateur.Role.Constructeur
                );
                sendUserNotification(constructeur.getId(), "Nouvelle équipe", message);

                // Notification pour le gestionnaire de stock
                Utilisateur gestionnaire = getUserByRoleAndId(
                        equipe.getGestionnaireStock().getGestionnairestock_id(),
                        Utilisateur.Role.GestionnaireStock
                );
                sendUserNotification(gestionnaire.getId(), "Nouvelle équipe", message);

                // Notifications pour les artisans
                for (Artisan artisan : equipe.getArtisans()) {
                    Utilisateur artisanUser = getUserByRoleAndId(
                            artisan.getArtisan_id(),
                            Utilisateur.Role.Artisan
                    );
                    sendUserNotification(artisanUser.getId(), "Nouvelle équipe", message);
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
    public void sendConversationMessage(int conversationId, int senderId, String content) throws SQLException {
        String sql = "INSERT INTO Message (conversation_id, expediteur_id, contenu) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setInt(2, senderId);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        }
    }

    // Récupérer les messages d'une conversation
    public List<Message> getConversationMessages(int conversationId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom as expediteur_nom FROM Message m "
                + "JOIN Utilisateur u ON m.expediteur_id = u.id "
                + "WHERE m.conversation_id = ? ORDER BY m.date_envoi ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setContenu(rs.getString("contenu"));
                message.setExpediteurId(rs.getInt("expediteur_id"));
                message.setDateEnvoi(rs.getTimestamp("date_envoi").toLocalDateTime());
                message.setLu(rs.getBoolean("lu"));
                messages.add(message);
            }
        }
        return messages;
    }

    // Récupérer les conversations d'un utilisateur
    public List<Conversation> getUserConversations(int userId) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT c.* FROM Conversation c "
                + "JOIN Conversation_Membre cm ON c.id = cm.conversation_id "
                + "WHERE cm.utilisateur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Conversation conv = new Conversation();
                conv.setId(rs.getInt("id"));
                conv.setEquipeId(rs.getInt("equipe_id"));
                conv.setNom(rs.getString("nom"));
                conv.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                conversations.add(conv);
            }
        }
        return conversations;
    }

    // Compter les messages non lus dans les conversations
    public int getUnreadConversationCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Message m "
                + "JOIN Conversation_Membre cm ON m.conversation_id = cm.conversation_id "
                + "WHERE cm.utilisateur_id = ? AND m.lu = false AND m.expediteur_id != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Marquer les messages comme lus
    public void markConversationMessagesAsRead(int conversationId, int userId) throws SQLException {
        String sql = "UPDATE Message SET lu = true "
                + "WHERE conversation_id = ? AND expediteur_id != ? AND lu = false";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }
    public void sendUserNotification(int userId, String title, String message) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Notification notification = new Notification(userId, title + ": " + message);
            new NotificationDAO().createNotification(notification);
        }
    }

    public Utilisateur getUserByRoleAndId(int id, Utilisateur.Role role) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE id = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, role.name());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Utilisateur user = UtilisateurDAO.mapUtilisateur(rs);

                // Charger les sous-entités en fonction du rôle
                switch (role) {
                    case Artisan:
                        user.setArtisan(UtilisateurDAO.getArtisanByUserId(user.getId()));
                        break;
                    case Constructeur:
                        user.setConstructeur(UtilisateurDAO.getConstructeurByUserId(user.getId()));
                        break;
                    case GestionnaireStock:
                        user.setGestionnaireDeStock(UtilisateurDAO.getGestionnaireStockByUserId(user.getId()));
                        break;
                }

                return user;
            } else {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID " + id + " et le rôle " + role);
            }
        }
    }
    public void sendUserNotification(int userId, String title, String message, String type, int referenceId) throws SQLException {
        Notification notification = new Notification(
                userId,
                title + ": " + message,
                type,
                referenceId
        );
        notificationDAO.createNotification(notification);
    }
    private Notification mapNotification(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("message"),
                rs.getString("type"), // Nouveau champ
                rs.getInt("reference_id"), // Nouveau champ
                rs.getBoolean("is_read"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("read_at") != null ? rs.getTimestamp("read_at").toLocalDateTime() : null
        );
    }

}