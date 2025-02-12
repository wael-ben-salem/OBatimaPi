package io.OurBatima.core.model;

import java.sql.Date;
import java.sql.Time;

public class Plannification {
    private int idPlannification;
    private int idTache;
    private Date datePlanifiee; // Correction ici
    private Time heurePlannification;
    private String priorite;

    public Plannification(int idPlannification, int idTache, Date datePlanifiee, Time heurePlannification, String priorite) {
        this.idPlannification = idPlannification;
        this.idTache = idTache;
        this.datePlanifiee = datePlanifiee;
        this.heurePlannification = heurePlannification;
        this.priorite = priorite;
    }

    public Plannification(int idTache, Date datePlanifiee, Time heurePlannification, String priorite) {
        this.idTache = idTache;
        this.datePlanifiee = datePlanifiee;
        this.heurePlannification = heurePlannification;
        this.priorite = priorite;
    }

    public int getIdPlannification() { return idPlannification; }
    public void setIdPlannification(int idPlannification) { this.idPlannification = idPlannification; }

    public int getIdTache() { return idTache; }
    public void setIdTache(int idTache) { this.idTache = idTache; }

    public Date getDatePlanifiee() { return datePlanifiee; } // Correction ici
    public void setDatePlanifiee(Date datePlanifiee) { this.datePlanifiee = datePlanifiee; } // Correction ici

    public Time getHeurePlannification() { return heurePlannification; }
    public void setHeurePlannification(Time heurePlannification) { this.heurePlannification = heurePlannification; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    @Override
    public String toString() {
        return "Plannification{" +
                "idPlannification=" + idPlannification +
                ", idTache=" + idTache +
                ", datePlanifiee=" + datePlanifiee + // Correction ici
                ", heurePlannification=" + heurePlannification +
                ", priorite='" + priorite + '\'' +
                '}';
    }
}
