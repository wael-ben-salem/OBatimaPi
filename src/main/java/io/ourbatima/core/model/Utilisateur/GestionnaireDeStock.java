package io.ourbatima.core.model.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;

public class GestionnaireDeStock extends Utilisateur{
    private int gestionnairestock_id;
    private  Utilisateur utilisateur;

    @Override
    public String toString() {
        return getNom() + " " + getPrenom(); // Retourne directement "Nom Prénom"
    }

    public GestionnaireDeStock(int id, String nom, String prenom, String email,
                               String motDePasse, String telephone, String adresse,
                               Utilisateur.Statut statut, boolean isConfirmed,  // Utiliser l'enum du modèle
                               Utilisateur.Role role,  // Utiliser l'enum du modèle
                               int gestionnairestock_id) {

        super(id, nom, prenom, email, motDePasse, telephone, adresse,
                statut, isConfirmed, role);
        this.gestionnairestock_id = gestionnairestock_id;

    }



    public GestionnaireDeStock(int gestionnairestock_id) {
        this.gestionnairestock_id = gestionnairestock_id;
    }

    public GestionnaireDeStock() {

    }
    public GestionnaireDeStock(int gestionnairestock_id,
                        Utilisateur utilisateur) {

        super(utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                utilisateur.getTelephone(),
                utilisateur.getAdresse(),
                utilisateur.getStatut(),
                utilisateur.isConfirmed(),
                utilisateur.getRole());

        this.gestionnairestock_id = gestionnairestock_id;

    }


    public int getGestionnairestock_id() {
        return gestionnairestock_id;
    }

    public void setGestionnairestock_id(int gestionnairestock_id) {
        this.gestionnairestock_id = gestionnairestock_id;
    }
}
