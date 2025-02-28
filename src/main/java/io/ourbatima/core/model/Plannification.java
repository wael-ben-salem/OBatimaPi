package io.ourbatima.core.model;

import java.sql.Date;
import java.sql.Time;

public class Plannification {
    private int idPlannification;
    private int idTache;
    private Date datePlanifiee;
    private Time heureDebut;
    private Time heureFin;
    private String priorite;
    private String remarques;
    private String statut;

    // Constructor with all fields
    public Plannification(int idPlannification, int idTache, Date datePlanifiee, Time heureDebut, Time heureFin, String priorite, String remarques, String statut) {
        this.idPlannification = idPlannification;
        this.idTache = idTache;
        this.datePlanifiee = datePlanifiee;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.priorite = priorite;
        this.remarques = remarques;
        this.statut = statut;
    }
    // Constructor with all fields including ID (for updates)
    public Plannification(int idPlannification, int idTache, Date datePlanifiee, Time heureDebut, Time heureFin, String remarques, String statut) {
        this.idPlannification = idPlannification;
        this.idTache = idTache;
        this.datePlanifiee = datePlanifiee;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.remarques = remarques;
        this.statut = statut;
    }

    // Constructor without ID (for inserts)
    public Plannification(int idTache, Date datePlanifiee, Time heureDebut, Time heureFin, String priorite, String remarques, String statut) {
        this.idTache = idTache;
        this.datePlanifiee = datePlanifiee;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.priorite = priorite;
        this.remarques = remarques;
        this.statut = statut;
    }

    public Plannification(int idTache, Date date, Time heureDebut, Time heureFin, String text) {
        this.idTache = idTache;
        this.datePlanifiee = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.remarques = text;
    }

    public Plannification(int planId, int i, Date date, Time heureDebut, Time heureFin, String remarques) {
        this.idPlannification = planId;
        this.idTache = i;
        this.datePlanifiee = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.remarques = remarques;
    }

    public Plannification(int idTache, Date date, Time heureDebut, Time heureFin, String remarques, String statut) {
        this.idTache = idTache;
        this.datePlanifiee = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.remarques = remarques;
        this.statut = statut;
    }

    public int getIdPlannification() { return idPlannification; }
    public void setIdPlannification(int idPlannification) { this.idPlannification = idPlannification; }

    public int getIdTache() { return idTache; }
    public void setIdTache(int idTache) { this.idTache = idTache; }

    public Date getDatePlanifiee() { return datePlanifiee; }
    public void setDatePlanifiee(Date datePlanifiee) { this.datePlanifiee = datePlanifiee; }

    public Time getHeureDebut() { return heureDebut; }
    public void setHeureDebut(Time heureDebut) { this.heureDebut = heureDebut; }

    public Time getHeureFin() { return heureFin; }
    public void setHeureFin(Time heureFin) { this.heureFin = heureFin; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public String getRemarques() { return remarques; }
    public void setRemarques(String remarques) { this.remarques = remarques; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Plannification{" +
                "idPlannification=" + idPlannification +
                ", idTache=" + idTache +
                ", datePlanifiee=" + datePlanifiee +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", priorite='" + priorite + '\'' +
                ", remarques='" + remarques + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
