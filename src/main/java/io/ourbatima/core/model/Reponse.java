package io.ourbatima.core.model;



import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private String description;
    private String statut;
    private LocalDateTime date;
    private int idReclamation;

    // Constructeur par défaut
    public Reponse() {
    }

    // Constructeur avec tous les paramètres
    public Reponse(int id, String description, String statut, LocalDateTime date, int idReclamation) {
        this.id = id;
        this.description = description;
        this.statut = statut;
        this.date = date;
        this.idReclamation = idReclamation;
    }

    // Constructeur sans l'ID (pour les insertions)
    public Reponse(String description, String statut, LocalDateTime date, int idReclamation) {
        this.description = description;
        this.statut = statut;
        this.date = date;
        this.idReclamation = idReclamation;
    }
    public Reponse(String description, String statut, LocalDateTime date) {
        this.description = description;
        this.statut = statut;
        this.date = date;

    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", date=" + date +
                ", idReclamation=" + idReclamation +
                '}';
    }
}