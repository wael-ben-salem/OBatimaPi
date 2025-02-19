package io.ourbatima.core.model;

import java.sql.Date;

public class Tache {
    private int idTache;
    private int idProjet;
    private Integer constructeurId;
    private Integer artisanId;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String etat;

    public Tache(int idTache, int idProjet, Integer constructeurId, Integer artisanId, String description, Date dateDebut, Date dateFin, String etat) {
        this.idTache = idTache;
        this.idProjet = idProjet;
        this.constructeurId = constructeurId;
        this.artisanId = artisanId;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etat = etat;
    }

    public Tache(int idProjet, Integer constructeurId, Integer artisanId, String description, Date dateDebut, Date dateFin, String etat) {
        this.idProjet = idProjet;
        this.constructeurId = constructeurId;
        this.artisanId = artisanId;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etat = etat;
    }

    public Tache(int idTache, String description) {
        this.idTache = idTache;
        this.description = description;
    }

    // Getters and Setters
    public int getIdTache() { return idTache; }
    public void setIdTache(int idTache) { this.idTache = idTache; }

    public int getIdProjet() { return idProjet; }
    public void setIdProjet(int idProjet) { this.idProjet = idProjet; }

    public Integer getConstructeurId() { return constructeurId; }
    public void setConstructeurId(Integer constructeurId) { this.constructeurId = constructeurId; }

    public Integer getArtisanId() { return artisanId; }
    public void setArtisanId(Integer artisanId) { this.artisanId = artisanId; }

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
        return description;  // Ensure ComboBox displays only the task description
    }
}
