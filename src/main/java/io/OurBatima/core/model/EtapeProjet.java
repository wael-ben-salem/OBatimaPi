package io.OurBatima.core.model;

import java.math.BigDecimal;
import java.sql.Date;

public class EtapeProjet {
    private int Id_etapeProjet;
    private int Id_projet;
    private String nomEtape;
    private String description;
    private Date dteDebut;
    private Date datefin;
    private String statut;
    private BigDecimal montant;

    public EtapeProjet(){}

    public EtapeProjet(int Id_projet, String nomEtape, String description, Date dteDebut, Date datefin, String statut, BigDecimal montant) {
        this.Id_projet = Id_projet;
        this.nomEtape = nomEtape;
        this.description = description;
        this.dteDebut = dteDebut;
        this.datefin = datefin;
        this.statut = statut;
        this.montant = montant;
    }

    public EtapeProjet(int Id_etapeProjet, int Id_projet, String nomEtape, String description, Date dteDebut, Date datefin, String statut, BigDecimal montant) {
        this.Id_etapeProjet = Id_etapeProjet;
        this.Id_projet = Id_projet;
        this.nomEtape = nomEtape;
        this.description = description;
        this.dteDebut = dteDebut;
        this.datefin = datefin;
        this.statut = statut;
        this.montant = montant;
    }

    // Getters and setters
    public int getId_etapeProjet() {
        return Id_etapeProjet;
    }

    public void setId_etapeProjet(int Id_etapeProjet) {
        this.Id_etapeProjet = Id_etapeProjet;
    }

    public int getId_projet() {
        return Id_projet;
    }

    public void setId_projet(int Id_projet) {
        this.Id_projet = Id_projet;
    }

    public String getNomEtape() {
        return nomEtape;
    }

    public void setNomEtape(String nomEtape) {
        this.nomEtape = nomEtape;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDteDebut() {
        return dteDebut;
    }

    public void setDteDebut(Date dteDebut) {
        this.dteDebut = dteDebut;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    @Override
    public String toString() {
        return "EtapeProjet{" +
                "Id_etapeProjet=" + Id_etapeProjet +
                ", Id_projet=" + Id_projet +
                ", nomEtape='" + nomEtape + '\'' +
                ", description='" + description + '\'' +
                ", dteDebut=" + dteDebut +
                ", datefin=" + datefin +
                ", statut='" + statut + '\'' +
                ", montant=" + montant +
                '}';
    }
}
