package io.ourbatima.core.Dao.FinanceService;

import io.ourbatima.core.model.financeModel.abonnement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class abonnementService {


    public List<abonnement> getAllAbonnement() {
        List<abonnement> abo = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT *   FROM abonnement";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                abonnement abonne = new abonnement(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
                abo.add(abonne);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return abo;
    }

    public void insertAbonnemant(abonnement abb) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO abonnement (id_abonnement, nom_abonnement, duree, prix) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, abb.getIdAbonnement());
            stmt.setString(2, abb.getNomAbonnement());
            stmt.setString(3, abb.getDuree());
            stmt.setDouble(4, abb.getPrix());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Abonnement inséré avec succès !");
            } else {
                System.out.println("Échec de l'insertion !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }}


        public void upadteAbonnement (abonnement abb){
            try (Connection conn = getConnection()) {
                String sql = "UPDATE abonnement SET nom_abonnement = ?, duree = ?, prix = ? WHERE id_abonnement = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, abb.getNomAbonnement());
                stmt.setString(2, abb.getDuree());
                stmt.setDouble(3, abb.getPrix());
                stmt.setInt(4, abb.getIdAbonnement());


                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Abonnement mis à jour avec succès !");
                } else {
                    System.out.println("Échec de la mise à jour : Aucun abonnement trouvé avec cet ID.");
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


public void DeliteAbonnement(abonnement abb) {

    try (Connection conn = getConnection()) {

        String sql = "DELETE FROM abonnement WHERE id_abonnement = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, abb.getIdAbonnement());
        int rowsDeleted = stmt.executeUpdate();



        if (rowsDeleted > 0) {
            System.out.println("Abonnement supprimé avec succès !");
        } else {
            System.out.println("Aucun abonnement trouvé avec cet ID.");
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

}
}