package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Utilisateur.TeamRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamRatingDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    public void saveRating(TeamRating rating) throws SQLException {
        String sql = "INSERT INTO TeamRating (team_id, client_id, rating) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getEquipe().getId());
            stmt.setInt(2, rating.getClient().getId());
            stmt.setDouble(3, rating.getRating());
            stmt.executeUpdate();
        }
    }

    public double getAverageRating(int teamId) throws SQLException {
        String sql = "SELECT SUM(rating) as total FROM TeamRating WHERE team_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                Equipe equipe = new EquipeDAO().findById(teamId);
                int memberCount = equipe.getMembres().size();
                return memberCount == 0 ? 0 : total / memberCount;
            }
            return 0;
        }
    }
    public boolean hasClientRated(int clientId, int teamId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TeamRating WHERE client_id = ? AND team_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            stmt.setInt(2, teamId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}