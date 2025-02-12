package io.OurBatima.core.Dao.FinanceService;

import io.OurBatima.core.model.financeModel.Contrat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.OurBatima.core.Dao.DatabaseConnection.getConnection;

public class ContratServise {

    public List<Contrat> getAllContart() {
        List<Contrat> contrat = new ArrayList<>();

        try (Connection conn = getConnection()) {

            String query = "SELECT *   FROM contrat";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Contrat con = new Contrat(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getString(5), rs.getDate(6), rs.getDouble(7), rs.getInt(8), rs.getInt(9), rs.getInt(10));
                contrat.add(con);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contrat;


    }

    public void insertContrat(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "INSERT INTO contrat (type_contrat, date_signature, date_debut, signature_electronique, date_fin, montant_total, id_client, id_projet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));
            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdClient());
            stmt.setInt(8, contrat.getIdProjet());
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Contrat inséré avec succès !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void insertContratConstructeur(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "INSERT INTO contrat (type_contrat, date_signature, date_debut, signature_electronique, date_fin, montant_total,id_constructeur, id_projet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));
            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdConstructeur());
            stmt.setInt(8, contrat.getIdProjet());
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Contrat inséré avec succès !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }




    public void update(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "UPDATE contrat SET type_contrat = ?, date_signature = ?, date_debut = ?, signature_electronique = ?, date_fin = ?, montant_total = ?, id_client = ?, id_projet = ? WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);


            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdClient());
            stmt.setInt(8, contrat.getIdProjet());
            stmt.setInt(9, contrat.getIdContrat()); // WHERE id_contrat = ?

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Contrat mis à jour avec succès !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void updateContratConstructeur(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "UPDATE contrat SET type_contrat = ?, date_signature = ?, date_debut = ?, signature_electronique = ?, date_fin = ?, montant_total = ?, id_constructeur = ?, id_projet = ? WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);


            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdConstructeur());
            stmt.setInt(8, contrat.getIdProjet());
            stmt.setInt(9, contrat.getIdContrat()); // WHERE id_contrat = ?

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Contrat mis à jour avec succès !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }



    public void delitecontrat(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "DELETE FROM contrat WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, contrat.getIdContrat());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Contrat supprimé avec succès !");
            } else {
                System.out.println("Aucun contrat trouvé avec cet ID !");
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }





}
