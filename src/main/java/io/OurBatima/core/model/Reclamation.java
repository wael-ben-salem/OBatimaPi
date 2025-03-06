package io.OurBatima.core.model;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private String description;
    private String statut;
    private LocalDateTime date;
    private int utilisateurId;

    // Constructeur par défaut
    public Reclamation() {}

    // Constructeur avec tous les champs
    public Reclamation(int id, String description, String statut, LocalDateTime date, int utilisateurId) {
        this.id = id;
        this.description = description;
        this.statut = statut;
        this.date = date;
        this.utilisateurId = utilisateurId;
    }

    public Reclamation( String description, String statut, LocalDateTime date, int utilisateurId) {

        this.description = description;
        this.statut = statut;
        this.date = date;
        this.utilisateurId = utilisateurId;
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatut() {
        return statut;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    // Méthode toString pour affichage
    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", date=" + date +
                ", utilisateurId=" + utilisateurId +
                '}';
    }
}
