package io.ourbatima.core.model.Utilisateur;

public class Constructeur  extends  Utilisateur{
    int constructeur_id ;
    private  Utilisateur utilisateur;
    private String specialite;
    private double salaireHeure;

    @Override
    public String toString() {
        return getNom() + " " + getPrenom(); // Retourne directement "Nom Prénom"
    }

    public Constructeur(int id, String nom, String prenom, String email,
                        String motDePasse, String telephone, String adresse,
                        Utilisateur.Statut statut, boolean isConfirmed,  // Utiliser l'enum du modèle
                        Utilisateur.Role role,  // Utiliser l'enum du modèle
                        int constructeur_id, String specialite, double salaireHeure) {

        super(id, nom, prenom, email, motDePasse, telephone, adresse,
                statut, isConfirmed, role);
        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }

    public Constructeur(int constructeur_id, String specialite, double salaireHeure) {
        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }
    public Constructeur(int constructeur_id,
                   Utilisateur utilisateur,
                   String  specialite,
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



        this.constructeur_id = constructeur_id;
        this.specialite = specialite;
        this.salaireHeure = salaire_heure;
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
    public Constructeur(String specialite, double salaireHeure) {
        this.specialite = specialite;
        this.salaireHeure = salaireHeure;
    }
}
