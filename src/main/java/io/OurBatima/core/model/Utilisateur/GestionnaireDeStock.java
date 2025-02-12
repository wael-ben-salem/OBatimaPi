package io.OurBatima.core.model.Utilisateur;

import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;

public class GestionnaireDeStock extends Utilisateur{
    private int gestionnairestock_id;

    public GestionnaireDeStock(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, UtilisateurDAO.Statut statut, boolean isConfirmed, UtilisateurDAO.Role role, int gestionnairestock_id) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.gestionnairestock_id = gestionnairestock_id;
    }

    public GestionnaireDeStock(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Statut statut, boolean isConfirmed, Role role, int gestionnairestock_id) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.gestionnairestock_id = gestionnairestock_id;
    }

    public GestionnaireDeStock(int gestionnairestock_id) {
        this.gestionnairestock_id = gestionnairestock_id;
    }

    public GestionnaireDeStock() {

    }

    public int getGestionnairestock_id() {
        return gestionnairestock_id;
    }

    public void setGestionnairestock_id(int gestionnairestock_id) {
        this.gestionnairestock_id = gestionnairestock_id;
    }
}
