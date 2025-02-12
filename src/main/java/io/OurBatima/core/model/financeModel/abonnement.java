package io.OurBatima.core.model.financeModel;


public class abonnement {
    private int idAbonnement;
    private String nomAbonnement;
    private String duree; // en mois, par exemple
    private double prix;

    // Constructeur
    public abonnement(int idAbonnement, String nomAbonnement, String duree, double prix) {
        this.idAbonnement = idAbonnement;
        this.nomAbonnement = nomAbonnement;
        this.duree = duree;
        this.prix = prix;
    }

    // Getters et Setters
    public int getIdAbonnement() {
        return idAbonnement;
    }

    public void setIdAbonnement(int idAbonnement) {
        this.idAbonnement = idAbonnement;
    }

    public String getNomAbonnement() {
        return nomAbonnement;
    }

    public void setNomAbonnement(String nomAbonnement) {
        this.nomAbonnement = nomAbonnement;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Abonnement{" +
                "idAbonnement=" + idAbonnement +
                ", nomAbonnement='" + nomAbonnement + '\'' +
                ", duree=" + duree +
                ", prix=" + prix +
                '}';
    }
}

