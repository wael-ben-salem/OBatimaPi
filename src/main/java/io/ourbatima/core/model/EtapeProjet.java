package io.ourbatima.core.model;

import java.math.BigDecimal;
import java.sql.Date;

public class EtapeProjet {
    private int Id_etapeProjet;
    private int Id_projet;
    private String nomEtape;
    private String description;
    private Date dateDebut;
    private Date datefin;
    private String statut;
    private BigDecimal montant;
    private Integer Id_rapport;

    public EtapeProjet(){}

    public EtapeProjet(int Id_projet, String nomEtape, String description, Date dateDebut, Date datefin, String statut, BigDecimal montant, Integer Id_rapport) {
        this.Id_projet = Id_projet;
        this.nomEtape = nomEtape;
        this.description = description;
        this.dateDebut = dateDebut;
        this.datefin = datefin;
        this.statut = statut;
        this.montant = montant;
        this.Id_rapport = Id_rapport;
    }

    public EtapeProjet(int Id_etapeProjet, int Id_projet, String nomEtape, String description, Date dateDebut, Date datefin, String statut, BigDecimal montant, Integer Id_rapport) {
        this.Id_etapeProjet = Id_etapeProjet;
        this.Id_projet = Id_projet;
        this.nomEtape = nomEtape;
        this.description = description;
        this.dateDebut = dateDebut;
        this.datefin = datefin;
        this.statut = statut;
        this.montant = montant;
        this.Id_rapport = Id_rapport;
    }

    public EtapeProjet(String nomEtape) {
        this.nomEtape = nomEtape;
    }


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

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDteDebut(Date dteDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
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

    public void setId_rapport(Integer id_rapport) {
        Id_rapport = id_rapport;
    }

    public Integer getId_rapport() {
        return Id_rapport;
    }

    @Override
    public String toString() {
        return "EtapeProjet{" +
                "Id_etapeProjet= " + Id_etapeProjet +
                ", Id_projet= " + Id_projet +
                ", nomEtape= " + nomEtape + '\'' +
                ", description= " + description + '\'' +
                ", dateDebut= " + dateDebut +
                ", datefin= " + datefin +
                ", statut=' " + statut + '\'' +
                ", montant= " + montant +
                ", Id_rapport= " + Id_rapport +
                '}';
    }
}
