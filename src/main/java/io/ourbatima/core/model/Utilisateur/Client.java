package io.ourbatima.core.model.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;

public class Client extends Utilisateur{
    private int client_id;

    public Client(int id, String nom, String prenom, String email,
                        String motDePasse, String telephone, String adresse,
                        Utilisateur.Statut statut, boolean isConfirmed,  // Utiliser l'enum du modèle
                        Utilisateur.Role role,  // Utiliser l'enum du modèle
                        int client_id ) {

        super(id, nom, prenom, email, motDePasse, telephone, adresse,
                statut, isConfirmed, role);
        this.client_id = client_id;

    }


   public Client(){

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
