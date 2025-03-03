package io.ourbatima.core.model;

import java.sql.Date;

public class Visite {
    private int idVisite;
    private int idTerrain;
    private Date dateVisite;
    private String observations;

    public Visite() {}

    public Visite(int idTerrain, String observations, Date dateVisite) {
        this.observations = observations;
        this.dateVisite = dateVisite;
        this.idTerrain = idTerrain;
    }

    public Visite(int idVisite, int idTerrain, String observations, Date dateVisite) {
        this.idVisite = idVisite;
        this.observations = observations;
        this.dateVisite = dateVisite;
        this.idTerrain = idTerrain;
    }

    public void setIdTerrain(int idTerrain) {
        this.idTerrain = idTerrain;
    }

    public int getIdTerrain() {
        return idTerrain;
    }

    public int getIdVisite() {
        return idVisite;
    }

    public Date getDateVisite() {
        return dateVisite;
    }

    public String getObservations() {
        return observations;
    }

    public void setIdVisite(int idVisite) {
        this.idVisite = idVisite;
    }

    public void setDateVisite(Date dateVisite) {
        this.dateVisite = dateVisite;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return "Visite{" +
                "idVisite=" + idVisite +
                ", idTerrain=" + idTerrain +
                ", dateVisite=" + dateVisite +
                ", observations='" + observations + '\'' +
                '}';
    }
}
