package io.ourbatima.core.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;

public class Equipe {
    private int id;
    private String nom;
    private Constructeur constructeur;
    private GestionnaireDeStock gestionnaireStock;
    private LocalDate dateCreation;
    private List<Artisan> artisans;

    public Equipe(int id, String nom, Constructeur constructeur, GestionnaireDeStock gestionnaireStock, LocalDate dateCreation, List<Artisan> artisans) {
        this.id = id;
        this.nom = nom;
        this.constructeur = constructeur;
        this.gestionnaireStock = gestionnaireStock;
        this.dateCreation = dateCreation;
        this.artisans = artisans;
    }
    public Equipe(){
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", constructeur=" + constructeur +
                ", gestionnaireStock=" + gestionnaireStock +
                ", dateCreation=" + dateCreation +
                ", artisans=" + artisans +
                '}';
    }

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

    public Constructeur getConstructeur() {
        return constructeur;
    }


    public void setConstructeur(Constructeur constructeur) {
        this.constructeur = constructeur;
    }

    public GestionnaireDeStock getGestionnaireStock() {
        return gestionnaireStock;
    }

    public void setGestionnaireStock(GestionnaireDeStock gestionnaireStock) {
        this.gestionnaireStock = gestionnaireStock;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Artisan> getArtisans() {
        return artisans;
    }

    public void setArtisans(List<Artisan> artisans) {
        this.artisans = artisans;
    }
}
