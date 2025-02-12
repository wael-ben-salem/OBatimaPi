
/*
 *
 *    Copyright (C) Gleidson Neves da Silveira
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.OurBatima;


import io.OurBatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.OurBatima.core.Dao.FinanceService.ContratServise;
import io.OurBatima.core.Dao.FinanceService.abonnementService;
import io.OurBatima.core.Dao.Projet.ProjetDAO;
import io.OurBatima.core.Dao.Stock.ArticleDAO;
import io.OurBatima.core.Dao.Stock.FournisseurDAO;
import io.OurBatima.core.Dao.Stock.StockDAO;
import io.OurBatima.core.Dao.Utilisateur.EquipeDAO;
import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.OurBatima.core.Dao.plannification.PlannificationDAO;
import io.OurBatima.core.Dao.taches.TacheDAO;
import io.OurBatima.core.Main;
import io.OurBatima.core.model.*;
import io.OurBatima.core.model.Utilisateur.*;
import io.OurBatima.core.model.financeModel.Contrat;
import io.OurBatima.core.model.financeModel.abonnement;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO.updateUser;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Create on  19/04/2023
 */
public class Starter extends Main {

    public static void main(String[] args) throws SQLException {


        Utilisateur utilisateur = new Utilisateur(
                1, "mahmoud", "krichen", "mahmoud.krichen@example.com", "1234567890",
                "MotDePasseSécurisé", "Paris", Utilisateur.Statut.en_attente, true,
                Utilisateur.Role.Artisan
        );
        Utilisateur utilisateur1 = new Utilisateur(
                2, "yassine", "abbes", "yassin.abbes@example.com", "1234567890",
                "22123456", "Paris", Utilisateur.Statut.en_attente, true,
                Utilisateur.Role.Constructeur
        );
        Utilisateur utilisateur2 = new Utilisateur(
                3, "mahdi", "ksouri", "mahdi.ksouri@example.com", "1234567890",
                "22123456", "Paris", Utilisateur.Statut.en_attente, true,
                Utilisateur.Role.GestionnaireStock
        );Utilisateur utilisateur3 = new Utilisateur(
                4, "sirine", "hjaij", "gdgdc.gg@example.com", "1234567890",
                "22123456", "Paris", Utilisateur.Statut.en_attente, true,
                Utilisateur.Role.Client
        );
        Utilisateur utilisateur4 = new Utilisateur(
                5, "wael", "bensalem", "wael.bensalem@example.com", "1234567890",
                "22123456", "Paris", Utilisateur.Statut.en_attente, true,
                Utilisateur.Role.Artisan
        );

        // Créer un artisan avec une spécialité et un salaire horaire
        Artisan artisan = new Artisan(1, Artisan.Specialite.Menuiserie, 20.0);
        Artisan artisan1 = new Artisan(5, Artisan.Specialite.Autre, 20.0);

        Constructeur con = new Constructeur(2, "Dir", 20.0);
        GestionnaireDeStock ges = new GestionnaireDeStock(3);
        Client clie = new Client(4);


        utilisateur.setArtisan(artisan);
        utilisateur1.setConstructeur(con);
        utilisateur2.setGestionnaireDeStock(ges);
        utilisateur3.setClient(clie);
        utilisateur4.setArtisan(artisan1);




        // Appel à la méthode saveUser
        UtilisateurDAO ud = new UtilisateurDAO();
        boolean result = UtilisateurDAO.saveUser(utilisateur);
        boolean result1 = UtilisateurDAO.saveUser(utilisateur1);
        boolean result2 = UtilisateurDAO.saveUser(utilisateur2);
        boolean result3 = UtilisateurDAO.saveUser(utilisateur3);
        boolean result4 = UtilisateurDAO.saveUser(utilisateur4);




        if (result && result1 && result2 &&result3 && result4 ) {
            System.out.println("Utilisateur eT SON TYPE créés avec succès !");
        } else {
            System.out.println("Erreur lors de la création de l'Utilisateur et SON TYPE.");
        }


        // update
        utilisateur.setNom("mahmoud");
        utilisateur.getArtisan().setSpecialite(Artisan.Specialite.Plomberie); // Modifier la spécialité
        utilisateur1.getConstructeur().setSpecialite("hamas"); // Modifier la spécialité


        boolean isUpdated = updateUser(utilisateur);
        boolean isUpdated1 = updateUser(utilisateur1);


        if (isUpdated && isUpdated1) {
            System.out.println("Utilisateur mis à jour avec succès !");
        } else {
            System.out.println("Échec de la mise à jour de l'utilisateur.");
        }


        // pour la supprission
        boolean isDeleted = ud.deleteUser(37);
        if (isDeleted) {
            System.out.println("Suppression réussie !");
        } else {
            System.out.println("Aucun utilisateur supprimé !");
        }
        EquipeDAO equipeDAO = new EquipeDAO();

        Equipe equipe = new Equipe();
        equipe.setNom("Équipe Alpha");
        equipe.setDateCreation(LocalDate.now());
        equipe.setConstructeur(con);
        equipe.setGestionnaireStock(ges);
        equipe.setArtisans(Arrays.asList(artisan1, artisan));

        // Insertion en base
        equipeDAO.create(equipe);
        System.out.println("Équipe créée avec ID : " + equipe.getId());

        // Récupération de l'équipe
        Equipe equipeRecuperee = equipeDAO.findById(equipe.getId());
        if (equipeRecuperee != null) {
            System.out.println("Équipe récupérée : " + equipeRecuperee.getNom());
        }

        // Suppression de l'équipe
        equipeDAO.delete(equipe.getId());
        System.out.println("Équipe supprimée avec ID : " + equipe.getId());






// Syrine

        ProjetDAO projetDAO = new ProjetDAO();
        Projet prj = new Projet(1, 1, new BigDecimal("50000.00"), "Residential", "Modern", "In Progress", new Date(System.currentTimeMillis()));
        projetDAO.addProjet(prj);
        System.out.println("New project added successfully.");


        EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();

        // 1. Add a new EtapeProjet
        EtapeProjet newEtape = new EtapeProjet(1, 1, "Foundation", "Laying the foundation",
                Date.valueOf("2025-02-01"), Date.valueOf("2025-04-01"), "In Progress", new BigDecimal("50000.00"));
        etapeProjetDAO.addEtapeProjet(newEtape);
        System.out.println("\nNew EtapeProjet added successfully!");





        final ContratServise Serviceab=new ContratServise()  ;
        final abonnementService abon=new abonnementService();
        System.out.println(Serviceab.getAllContart());
        Contrat contrat1 = new Contrat(
                1, // idContrat
                "Contrat de construction",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),

                "Signature123",
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)), // dateFin (1 an après)
                50000.0,
                4,

                1);
        // idConstructeur
        Serviceab.insertContrat(contrat1);
        contrat1.setMontantTotal(5800);
        Serviceab.update(contrat1);
        // Serviceab.delitecontrat(con);

        System.out.println(Serviceab.getAllContart());

        abonnement abo1 = new abonnement(1, "Premium", "12 mois", 99.99);
        abonnement abo2 = new abonnement(2, "Standard", "6 mois", 49.99);
        abon.insertAbonnemant(abo2);
        abo1.setDuree("17 mois");
        abon.upadteAbonnement(abo1);
        abon.DeliteAbonnement(abo1);
        System.out.println(abon.getAllAbonnement());




// yassine


        StockDAO stockDAO = new StockDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        FournisseurDAO fournisseurDAO = new FournisseurDAO();

        // Test CRUD operations for Stock
        // Create a new stock
        Stock newStock = new Stock(1, "Stock A", "Location A", "2023-01-01");
        boolean isStockSaved = stockDAO.saveStock(newStock);
        if (isStockSaved) {
            System.out.println("Stock saved successfully!");
        } else {
            System.out.println("Failed to save stock.");
        }

        // Read the stock by ID
        Stock fetchedStock = stockDAO.getStockById(1); // Assuming ID 1 exists
        if (fetchedStock != null) {
            System.out.println("Fetched Stock: " + fetchedStock.getNom());
        } else {
            System.out.println("Stock not found.");
        }

        // Update the stock
        if (fetchedStock != null) {
            fetchedStock.setEmplacement("Updated Location");
            boolean isStockUpdated = stockDAO.updateStock(fetchedStock);
            if (isStockUpdated) {
                System.out.println("Stock updated successfully!");
            } else {
                System.out.println("Failed to update stock.");
            }
        }

        // Delete the stock
        // boolean isDeletedStock = stockDAO.deleteStock(1); // Assuming ID 1 exists
        //if (isDeletedStock) {
        //  System.out.println("Stock deleted successfully!");
        //} else {
        //  System.out.println("Failed to delete stock.");
        //}


        Fournisseur newFournisseur = new Fournisseur(1, "John", "Doe", "john.doe@example.com", "1234567890", "123 Main St");
        boolean isFournisseurSaved = fournisseurDAO.saveFournisseur(newFournisseur);
        if (isFournisseurSaved) {
            System.out.println("Fournisseur saved successfully!");
        } else {
            System.out.println("Failed to save fournisseur.");
        }

        // Read the fournisseur by ID
        Fournisseur fetchedFournisseur = fournisseurDAO.getFournisseurById(1); // Assuming ID 1 exists
        if (fetchedFournisseur != null) {
            System.out.println("Fetched Fournisseur: " + fetchedFournisseur.getNom() + " " + fetchedFournisseur.getPrenom());
        } else {
            System.out.println("Fournisseur not found.");
        }

        // Update the fournisseur
        if (fetchedFournisseur != null) {
            fetchedFournisseur.setEmail("updated.email@example.com");
            boolean isFournisseurUpdated = fournisseurDAO.updateFournisseur(fetchedFournisseur);
            if (isFournisseurUpdated) {
                System.out.println("Fournisseur updated successfully!");
            } else {
                System.out.println("Failed to update fournisseur.");
            }
        }

        // Delete the fournisseur
        //boolean isDeletedFournisseur = fournisseurDAO.deleteFournisseur(1); // Assuming ID 1 exists
        // if (isDeletedFournisseur) {
        //   System.out.println("Fournisseur deleted successfully!");
        // } else {
        //   System.out.println("Failed to delete fournisseur.");
        //}






        // Test CRUD operations for Article
        // Create a new article
        Article newArticle = new Article(1, "Test Article", "Description of test article", "10.99", "http://example.com/photo.png", 1, 1, 1);
        boolean isSaved = articleDAO.saveArticle(newArticle);
        if (isSaved) {
            System.out.println("Article saved successfully!");
        } else {
            System.out.println("Failed to save article.");
        }

        // Read the article by ID
        Article fetchedArticle = articleDAO.getArticleById(2);
        if (fetchedArticle != null) {
            System.out.println("Fetched Article: " + fetchedArticle.getNom());
        } else {
            System.out.println("Article not found.");
        }

        // Update the article
        if (fetchedArticle != null) {
            fetchedArticle.setDescription("Updated description.");
            boolean isUpdatedstock = articleDAO.updateArticle(fetchedArticle);
            if (isUpdatedstock) {
                System.out.println("Article updated successfully!");
            } else {
                System.out.println("Failed to update article.");
            }
        }

        // Delete the article
        //boolean isDeletedArticle = articleDAO.deleteArticle(1); // Assuming ID 1 exists
        //if (isDeletedArticle) {
        //  System.out.println("Article deleted successfully!");
        //} else {
        //  System.out.println("Failed to delete article.");
        //}

        // Test CRUD operations for Fournisseur
        // Create a new fournisseur

// mahdi ksouri

      TacheDAO  tacheDAO = new TacheDAO();

        // Ajouter une tâche
        System.out.println("Ajout d'une nouvelle tâche...");
        Tache nouvelleTache = new Tache(1,2,1,
                "Peindre la porte d'entrée", Date.valueOf("2024-02-10"), Date.valueOf("2024-02-15"), "En attente");
        tacheDAO.ajouterTache(nouvelleTache);

        // Afficher toutes les tâches
        System.out.println("Récupération des tâches...");
        List<Tache> taches = tacheDAO.getAllTaches();
        System.out.println("Liste des tâches :");
        for (Tache t : taches) {
            System.out.println(t);
        }

        // Modifier une tâche
        if (!taches.isEmpty()) {
            Tache tacheAModifier = taches.get(0); // Modifier la première tâche
            System.out.println("Modification de la tâche avec ID : " + tacheAModifier.getIdTache());
            tacheAModifier.setDescription("Peindre la porte d'entrée en bleu"); // Nouvelle description
            tacheAModifier.setEtat("En cours"); // Changement d'état
            tacheDAO.modifierTache(tacheAModifier);
            System.out.println("Tâche modifiée avec succès !");
        }



// Add a new Plannification
        PlannificationDAO plannificationDAO = new PlannificationDAO();
        System.out.println("Ajout d'une nouvelle plannification...");
        Plannification nouvellePlannification = new Plannification(1, Date.valueOf("2024-02-10"), java.sql.Time.valueOf("10:00:00"), "Haute");
        plannificationDAO.ajouterPlannification(nouvellePlannification);

        // Display all plannifications
        System.out.println("Liste des plannifications :");
        List<Plannification> plannifications = plannificationDAO.getAllPlannifications();
        for (Plannification p : plannifications) {
            System.out.println(p);
        }


    }











                }






