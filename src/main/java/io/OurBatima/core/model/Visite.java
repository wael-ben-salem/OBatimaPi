package io.ourbatima.core.model;

import java.sql.Date;

public class Visite {
    private int Id_visite;
    private int Id_projet;
    private Date dateVisite;
    private String observations;

    public Visite(){}

    public Visite(int Id_projet, String observations, Date dateVisite) {
        this.observations = observations;
        this.dateVisite = dateVisite;
        this.Id_projet = Id_projet;
    }

    public Visite(int id_visite, int Id_projet, String observations, Date dateVisite) {
        Id_visite = id_visite;
        this.observations = observations;
        this.dateVisite = dateVisite;
        this.Id_projet = Id_projet;
    }

    public void setId_projet(int id_projet) {
        Id_projet = id_projet;
    }

    public int getId_projet() {
        return Id_projet;
    }

    public int getId_visite() {
        return Id_visite;
    }

    public Date getDateVisite() {
        return dateVisite;
    }

    public String getObservations() {
        return observations;
    }

    public void setId_visite(int id_visite) {
        Id_visite = id_visite;
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
                "Id_visite=" + Id_visite +
                ", Id_projet=" + Id_projet +
                ", dateVisite=" + dateVisite +
                ", observations='" + observations + '\'' +
                '}';
    }
}
