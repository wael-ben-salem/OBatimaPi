package io.ourbatima.core.services;

import io.ourbatima.core.Dao.DatabaseConnection;
import java.sql.*;
import java.time.*;
import java.util.Timer;
import java.util.TimerTask;

public class PlannificationStatusUpdater {

    // Set Tunisian TimeZone
    private static final ZoneId TUNISIA_ZONE = ZoneId.of("Africa/Tunis");

    public static void startScheduler() {
        Timer timer = new Timer(true); // Run as a background daemon thread
        timer.scheduleAtFixedRate(new UpdateTask(), 0, 60 * 1000); // Runs every minute
    }

    static class UpdateTask extends TimerTask {
        @Override
        public void run() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                LocalDateTime now = LocalDateTime.now(TUNISIA_ZONE);
                Time currentTime = Time.valueOf(now.toLocalTime());
                Date currentDate = Date.valueOf(now.toLocalDate());

                // Update "En cours" status
                String updateEnCours = """
                    UPDATE Plannification 
                    SET statut = 'En cours' 
                    WHERE date_planifiee = ? 
                    AND heure_debut <= ? 
                    AND heure_fin > ? 
                    AND statut = 'Planifié'
                """;

                // Update "Terminé" status
                String updateTermine = """
                    UPDATE Plannification 
                    SET statut = 'Terminé' 
                    WHERE date_planifiee = ? 
                    AND heure_fin <= ? 
                    AND statut = 'En cours'
                """;

                try (PreparedStatement stmt1 = conn.prepareStatement(updateEnCours);
                     PreparedStatement stmt2 = conn.prepareStatement(updateTermine)) {

                    // Set parameters and execute the first update
                    stmt1.setDate(1, currentDate);
                    stmt1.setTime(2, currentTime);
                    stmt1.setTime(3, currentTime);
                    stmt1.executeUpdate();

                    // Set parameters and execute the second update
                    stmt2.setDate(1, currentDate);
                    stmt2.setTime(2, currentTime);
                    stmt2.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
