package io.ourbatima.core.model.Utilisateur;


import java.time.LocalDateTime;

public class TeamRating {
    private int id;
    private Equipe equipe;
    private Utilisateur client;
    private double rating;
    private LocalDateTime createdAt;

    public TeamRating(int id, Equipe equipe, Utilisateur client, double rating, LocalDateTime createdAt) {
        this.id = id;
        this.equipe = equipe;
        this.client = client;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public TeamRating() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}