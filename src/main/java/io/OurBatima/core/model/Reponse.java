package io.OurBatima.core.model;


import java.time.LocalDateTime;

public class Reponse {
    int id,reclamationId;
    String description,statut;
    LocalDateTime date;


    // Constructeur sans paramétres
    public Reponse() {

    }



    // Constructeur avec paramètres
    public Reponse(int id, int reclamationId, String description, String statut, LocalDateTime date) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.description = description;
        this.statut = statut;
        this.date = date;
    }

    // Constructeur sans iD
    public Reponse(int reclamationId, String description, String statut) {
        this.reclamationId = reclamationId;
        this.description = description;
        this.statut = statut;
        this.date = LocalDateTime.now();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getReclamationId() {
        return reclamationId;
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

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
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

}
