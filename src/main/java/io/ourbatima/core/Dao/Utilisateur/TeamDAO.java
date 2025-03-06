package io.ourbatima.core.Dao.Utilisateur;

import io.ourbatima.core.Dao.DatabaseConnection;

import java.sql.*;

public class TeamDAO {
    private Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public int createTeam(String teamName, int creatorId) throws SQLException {
        String sql = "INSERT INTO teams (team_name, created_by) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, teamName);
            pstmt.setInt(2, creatorId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int teamId = rs.getInt(1);
                    new GroupDAO().createGroup(teamId, "Discussion d'équipe: " + teamName);
                    return teamId;
                }
            }
        }
        return -1;
    }

}