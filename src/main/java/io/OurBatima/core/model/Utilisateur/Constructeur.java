package io.OurBatima.core.model.Utilisateur;

import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;

public class Constructeur  extends  Utilisateur{
    int constructeur_id ;
    private String specialite;
    private double salaireHeure;

    public Constructeur(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, UtilisateurDAO.Statut statut, boolean isConfirmed, UtilisateurDAO.Role role, int constructeur_id, String specialite, double salaireHeure) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }

    public Constructeur(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Statut statut, boolean isConfirmed, Role role, int constructeur_id, String specialite, double salaireHeure) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }

    public Constructeur(int constructeur_id, String specialite, double salaireHeure) {
        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }

    public Constructeur() {

    }

    public int getConstructeur_id() {
        return constructeur_id;
    }

    public void setConstructeur_id(int constructeur_id) {
        this.constructeur_id = constructeur_id;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public double getSalaireHeure() {
        return salaireHeure;
    }

    public void setSalaireHeure(double salaireHeure) {
        this.salaireHeure = salaireHeure;
    }
}
