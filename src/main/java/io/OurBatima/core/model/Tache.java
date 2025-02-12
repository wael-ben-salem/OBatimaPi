package io.OurBatima.core.model;

import java.sql.Date;

public class Tache {
    private int idTache;
    private int idProjet;
    private Integer userIdConstructeur;
    private Integer userIdArtisan;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String etat;

    public Tache(int idTache, int idProjet, Integer userIdConstructeur, Integer userIdArtisan, String description, Date dateDebut, Date dateFin, String etat) {
        this.idTache = idTache;
        this.idProjet = idProjet;
        this.userIdConstructeur = userIdConstructeur;
        this.userIdArtisan = userIdArtisan;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etat = etat;
    }

    public Tache(int idProjet, Integer userIdConstructeur, Integer userIdArtisan, String description, Date dateDebut, Date dateFin, String etat) {
        this.idProjet = idProjet;
        this.userIdConstructeur = userIdConstructeur;
        this.userIdArtisan = userIdArtisan;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etat = etat;
    }

    // Getters et Setters
    public int getIdTache() { return idTache; }
    public void setIdTache(int idTache) { this.idTache = idTache; }

    public int getIdProjet() { return idProjet; }
    public void setIdProjet(int idProjet) { this.idProjet = idProjet; }

    public Integer getUserIdConstructeur() { return userIdConstructeur; }
    public void setUserIdConstructeur(Integer userIdConstructeur) { this.userIdConstructeur = userIdConstructeur; }

    public Integer getUserIdArtisan() { return userIdArtisan; }
    public void setUserIdArtisan(Integer userIdArtisan) { this.userIdArtisan = userIdArtisan; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    @Override
    public String toString() {
        return "Tache{" +
                "idTache=" + idTache +
                ", idProjet=" + idProjet +
                ", userIdConstructeur=" + userIdConstructeur +
                ", userIdArtisan=" + userIdArtisan +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", etat='" + etat + '\'' +
                '}';
    }
}
