package io.ourbatima.core.model;

public class Fournisseur {
    private int fournisseur_id;
    private String nom;
    private String prenom;
    private String email;
    private String numeroDeTelephone;
    private String adresse;

    public Fournisseur(int fournisseur_id, String nom, String prenom, String email, String numeroDeTelephone, String adresse) {
        this.fournisseur_id = fournisseur_id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numeroDeTelephone = numeroDeTelephone;
        this.adresse = adresse;
    }

    public Fournisseur() {
        // Default constructor
    }

    // Getters and setters
    public int getId() {
        return fournisseur_id;
    }

    public void setId(int fournisseur_id) {
        this.fournisseur_id = fournisseur_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroDeTelephone() {
        return numeroDeTelephone;
    }

    public void setNumeroDeTelephone(String numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }


    public String toString() {
        return String.format(
                "fournisseur_id=%d, nom='%s', prenom='%s', email='%s', numeroDeTelephone='%s', adresse='%s'",
                fournisseur_id, nom, prenom, email, numeroDeTelephone, adresse
        );
    }

}