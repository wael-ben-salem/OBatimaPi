package io.ourbatima.controllers;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.NotificationDAO;
import io.ourbatima.core.model.Utilisateur.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessagingService {

    private final EquipeDAO equipeDAO = new EquipeDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

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
}