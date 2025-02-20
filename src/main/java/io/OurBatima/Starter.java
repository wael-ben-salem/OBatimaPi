

package io.OurBatima;

import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.Main;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;



public class Starter extends Main {


    public static void main(String[] args) {
        launch(args);
        // Créer des instances de ReclamationDAO et ReponseDAO
        ReclamationDAO reclamationDAO = new ReclamationDAO();
        ReponseDAO reponseDAO = new ReponseDAO();

        // Ajouter une réclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setDescription("Problème de connexion internet");
        reclamation.setStatut("En cours");
        reclamation.setDate(LocalDateTime.now());
        reclamationDAO.add(reclamation);
        System.out.println("Réclamation ajoutée avec succès !");

        // Récupérer toutes les réclamations
        System.out.println("Liste des réclamations :");
        List<Reclamation> reclamations = reclamationDAO.getAll();
        for (Reclamation r : reclamations) {
            System.out.println(r);
        }

        // Ajouter une réponse à la première réclamation
        if (!reclamations.isEmpty()) {
            Reclamation firstReclamation = reclamations.get(0);

            Reponse reponse = new Reponse();
            reponse.setReclamationId(firstReclamation.getId());
            reponse.setDescription("Nous travaillons sur votre problème.");
            reponse.setStatut("En cours");
            reponse.setDate(LocalDateTime.now());
            reponseDAO.add(reponse);
            System.out.println("Réponse ajoutée avec succès !");
        }

        // Récupérer toutes les réponses
        System.out.println("Liste des réponses :");
        List<Reponse> reponses = reponseDAO.getAll();
        for (Reponse r : reponses) {
            System.out.println(r);
        }

        // Mettre à jour une réclamation
        if (!reclamations.isEmpty()) {
            Reclamation firstReclamation = reclamations.get(0);
            firstReclamation.setDescription("Problème résolu");
            firstReclamation.setStatut("Terminé");
            reclamationDAO.update(firstReclamation);
            System.out.println("Réclamation mise à jour avec succès !");
        }

        // Mettre à jour une réponse
        if (!reponses.isEmpty()) {
            Reponse firstReponse = reponses.get(0);
            firstReponse.setDescription("Le problème a été résolu.");
            firstReponse.setStatut("Terminé");
            reponseDAO.update(firstReponse);
            System.out.println("Réponse mise à jour avec succès !");
        }

        // Supprimer une réclamation et ses réponses associées
        if (!reclamations.isEmpty()) {
            int idToDelete = reclamations.get(0).getId();
            reponseDAO.deleteByReclamationId(idToDelete); // Supprimer les réponses associées
            reclamationDAO.delete(idToDelete); // Supprimer la réclamation
            System.out.println("Réclamation et réponses associées supprimées avec succès !");
        }
    }
}


