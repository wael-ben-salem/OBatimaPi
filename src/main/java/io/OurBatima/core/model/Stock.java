package io.ourbatima.core.model;

public class Stock {
    private int id;
    private String nom;
    private String emplacement;
    private String dateCreation;

    public Stock(int id, String nom, String emplacement, String dateCreation) {
        this.id = id;
        this.nom = nom;
        this.emplacement = emplacement;
        this.dateCreation = dateCreation;
    }

    public Stock() {
        // Default constructor
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
}