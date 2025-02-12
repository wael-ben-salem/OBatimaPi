package io.OurBatima.core.model;

public class Article {
    private int id;
    private String nom;
    private String description;
    private String prixUnitaire;
    private String photo;
    private int stockId; // Foreign key from Stock table
    private int fournisseurId; // Foreign key from Fournisseur table
    private int etapeProjetId; // Foreign key from EtapeProjet table

    public Article(int id, String nom, String description, String prixUnitaire, String photo, int stockId, int fournisseurId, int etapeProjetId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.photo = photo;
        this.stockId = stockId;
        this.fournisseurId = fournisseurId;
        this.etapeProjetId = etapeProjetId;
    }

    public Article() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(String prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public int getEtapeProjetId() {
        return etapeProjetId;
    }

    public void setEtapeProjetId(int etapeProjetId) {
        this.etapeProjetId = etapeProjetId;
    }
}