package io.ourbatima.core.Dao.FinanceService;

import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.Contrat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class ContratServise {

    public List<Contrat> getAllContart() {
        List<Contrat> contrat = new ArrayList<>();

        try (Connection conn = getConnection()) {

            String query = "SELECT *   FROM contrat";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Contrat con = new Contrat(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getString(5), rs.getDate(6), rs.getDouble(7), rs.getInt(8));


                contrat.add(con);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contrat;


    }

    public void insertContrat(Contrat contrat) {

        try (Connection conn = getConnection()) {

            String sql = "INSERT INTO contrat (type_contrat, date_signature, date_debut, signature_electronique, date_fin, montant_total, Id_projet) VALUES (?, ?, ?, ?, ?, ?,  ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, contrat.getDateSignature());
            stmt.setDate(3, contrat.getDateDebut());
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, contrat.getDateFin());
            stmt.setDouble(6, contrat.getMontantTotal());

            stmt.setInt(7, contrat.getIdProjet());
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

            String sql = "INSERT INTO contrat (type_contrat, date_signature, date_debut, signature_electronique, date_fin, montant_total, Id_projet) VALUES (?, ?, ?, ?, ?, ?,  ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));
            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdProjet());
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

            String sql = "UPDATE contrat SET type_contrat = ?, date_signature = ?, date_debut = ?, signature_electronique = ?, date_fin = ?, montant_total = ?,  Id_projet = ? WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);


            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));
            stmt.setDouble(6, contrat.getMontantTotal());

            stmt.setInt(7, contrat.getIdProjet());
            stmt.setInt(8, contrat.getIdContrat()); // WHERE id_contrat = ?

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

            String sql = "UPDATE contrat SET type_contrat = ?, date_signature = ?, date_debut = ?, signature_electronique = ?, date_fin = ?, montant_total = ?,  Id_projet = ? WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);


            stmt.setString(1, contrat.getTypeContrat());
            stmt.setDate(2, new Date(contrat.getDateSignature().getTime()));
            stmt.setDate(3, new Date(contrat.getDateDebut().getTime()));
            stmt.setString(4, contrat.isSignatureElectronique());
            stmt.setDate(5, new Date(contrat.getDateFin().getTime()));
            stmt.setDouble(6, contrat.getMontantTotal());
            stmt.setInt(7, contrat.getIdProjet());
            stmt.setInt(8, contrat.getIdContrat()); // WHERE id_contrat = ?

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Contrat mis à jour avec succès !");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


    public void delitecontrat(int id) {

        try (Connection conn = getConnection()) {

            String sql = "DELETE FROM contrat WHERE id_contrat = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
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

    public String getClientNOMEtidbyidcontrat(int idprojet) throws SQLException {
        System.out.println("idprojet: " + idprojet);
        String aloalo;
        String nom = "";
        int clientId = 0;
        try (Connection conn = getConnection()) {


            String query = "SELECT cl.nom, cl.id " +
                    "FROM utilisateur cl " +
                    "WHERE cl.role = 'Client' " +
                    "AND cl.id = ( " +
                    "    SELECT p.id_client  " +
                    "    FROM projet p " +
                    "    WHERE p.Id_projet  = ? " +
                    ");";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idprojet);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                nom = rs.getString("nom");
                clientId = rs.getInt("id");
                System.out.println(" ID: " + clientId + "Client: " + nom);
            }


        }

        return aloalo = clientId + "-" + nom;


    }

    public String getmailClientbyid(int id) {
        String mail = "";
        try (Connection conn = getConnection()) {


            String query = "SELECT email " +
                    "FROM utilisateur " +
                    "WHERE id = ? ";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                mail = rs.getString("email");

                System.out.println("Client: " + mail);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mail;

    }


    public Utilisateur getclientbyid(int idclient) {
        Utilisateur clt = new Utilisateur();

        try (Connection conn = getConnection()) {


            String query = "SELECT * " +
                    "FROM utilisateur " +
                    "WHERE id = ? ";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idclient);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clt.setNom(rs.getString("nom"));
                clt.setAdresse(rs.getString("adresse"));
                clt.setPrenom(rs.getString("prenom"));
                clt.setEmail(rs.getString("email"));
                clt.setId(idclient);
                clt.setTelephone(rs.getString("telephone"));


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clt;

    }


    public Projet getProjetbyid(int idprojet) {
        Projet proj = new Projet();
        try (Connection conn = getConnection()) {


            String query = "SELECT * " +
                    "FROM projet " +
                    "WHERE Id_projet = ? ";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idprojet);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
proj.setId_projet(rs.getInt("Id_projet"));
proj.setNomProjet(rs.getString("nomProjet"));
proj.setStyleArch(rs.getString("styleArch"));
proj.setType(rs.getString("type"));

            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

     return proj;
    }

}
