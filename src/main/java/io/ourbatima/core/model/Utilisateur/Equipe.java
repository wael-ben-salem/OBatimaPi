package io.ourbatima.core.model.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.TeamRatingDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Equipe {
    private int id;
    private String nom;
    private transient EquipeDAO equipeDAO = new EquipeDAO(); // Transient to prevent serialization issues

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
        StringBuilder sb = new StringBuilder();
        sb.append("Equipe{")
                .append("id=").append(id)
                .append(", nom='").append(nom).append('\'')
                .append(", constructeur=").append(constructeur != null ? constructeur.getNom() + " " + constructeur.getPrenom() : "null")
                .append(", gestionnaireStock=").append(gestionnaireStock != null ? gestionnaireStock.getNom() + " " + gestionnaireStock.getPrenom() : "null")
                .append(", dateCreation=").append(dateCreation)
                .append(", artisans=[");

        if (artisans != null && !artisans.isEmpty()) {
            for (Artisan artisan : artisans) {
                sb.append(artisan.getNom()).append(" ").append(artisan.getPrenom()).append(", ");
            }
            sb.setLength(sb.length() - 2); // Supprimer la dernière virgule
        }
        sb.append("]}");

        return sb.toString();
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
    public List<Utilisateur> getMembres() {
        List<Utilisateur> membres = new ArrayList<>();

        if (constructeur != null) {
            membres.add(constructeur);
        }
        if (gestionnaireStock != null) {
            membres.add(gestionnaireStock);
        }
        if (artisans != null) {
            membres.addAll(artisans);
        }

        return membres;
    }
    public double getAverageRating() {
        try {
            return new TeamRatingDAO().getAverageRating(this.id);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Utilisateur> getMembre() {
        try {
            return equipeDAO.getTeamMembers(this.id); // Use the DAO's proper query
        } catch (SQLException e) {
            System.err.println("Error fetching team members: " + e.getMessage());
            return Collections.emptyList();
        }

    }
}