package io.OurBatima.core.model.Utilisateur;

import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;

public class Client extends Utilisateur{
    private int client_id;

    public Client(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, UtilisateurDAO.Statut statut, boolean isConfirmed, UtilisateurDAO.Role role, int client_id) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.client_id = client_id;
    }

    public Client(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Statut statut, boolean isConfirmed, Role role, int client_id) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.client_id = client_id;
    }

    public Client(int client_id) {
        this.client_id = client_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
