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

    public Artisan(int artisan_id, Utilisateur utilisateur) {
        super(utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(),
                utilisateur.getTelephone(), utilisateur.getAdresse(), utilisateur.getMotDePasse(),
                utilisateur.getRole());
        this.artisan_id = artisan_id;
    }



    // Constructeur avec Utilisateur existant
    public Artisan(int artisan_id,
                   Utilisateur utilisateur,
                   Specialite specialite,
                   double salaire_heure) {

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

        this.artisan_id = artisan_id;
        this.specialite = specialite;
        this.salaireHeure = salaire_heure;
    }


    public enum Specialite {
        Menuiserie,Maçonnerie,Électricité,Plomberie,Autre
    }


    public Artisan(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Utilisateur.Statut statut, boolean isConfirmed, Utilisateur.Role role, Specialite specialite, double salaireHeure) {
        super(id, nom, prenom, email, motDePasse, telephone, adresse, statut, isConfirmed, role);
        setRole(Role.Artisan);

        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }
    public Artisan(int id, String nom, String prenom, String email,
                   String motDePasse, String telephone, String adresse,
                   Utilisateur.Statut statut, boolean isConfirmed,  // Utiliser l'enum du modèle
                   Utilisateur.Role role,  // Utiliser l'enum du modèle
                   int artisan_id, Specialite specialite, double salaireHeure) {

        super(id, nom, prenom, email, motDePasse, telephone, adresse,
                statut, isConfirmed, role);
        this.artisan_id = artisan_id;
        this.specialite = specialite;
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


    public String toString() {
        return getNom() + " " + getPrenom(); // Retourne directement "Nom Prénom"
    }
    public Artisan(Specialite specialite, double salaireHeure) {
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }
}