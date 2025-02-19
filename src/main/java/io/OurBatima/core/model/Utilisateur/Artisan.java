package io.ourbatima.core.model.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;

public class Artisan extends Utilisateur{
    int artisan_id ;
    private Specialite specialite;
    private double salaireHeure;

    public Artisan() {

    }

    public Artisan(int artisan_id, String nom) {
    }


    public enum Specialite {
        Menuiserie,Maçonnerie,Électricité,Plomberie,Autre
    }


    public Artisan(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, UtilisateurDAO.Statut statut, boolean isConfirmed, UtilisateurDAO.Role role, Specialite specialite, double salaireHeure) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        setRole(Role.Artisan);

        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }

    public Artisan(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Statut statut, boolean isConfirmed, Role role, int artisan_id,Specialite specialite, double salaireHeure) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        this.artisan_id =artisan_id;

        this.specialite = specialite;
        setRole(Role.Artisan);

        this.salaireHeure = salaireHeure;
    }

    public Artisan(int artisan_id,Specialite specialite, double salaireHeure) {
        this.artisan_id= artisan_id;
        this.specialite = specialite;
        setRole(Role.Artisan);
        this.salaireHeure = salaireHeure;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public int getArtisan_id() {
        return artisan_id;
    }

    public void setArtisan_id(int artisan_id) {
        this.artisan_id = artisan_id;
    }

    public double getSalaireHeure() {
        return salaireHeure;
    }

    public void setSalaireHeure(double salaireHeure) {
        this.salaireHeure = salaireHeure;
    }
}
