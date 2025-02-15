package io.OurBatima.core.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Projet {
    private int Id_projet;
    private String nomProjet;
    private int Id_equipe;
    private int id_client;
    private BigDecimal budget;
    private String type;
    private String styleArch;
    private String etat;
    private Timestamp dateCreation;

    public Projet() {}

    public Projet(int Id_equipe, String nomProjet, int id_client, String type, String styleArch, BigDecimal budget, String etat, Timestamp dateCreation) {
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.nomProjet = nomProjet;
        this.type = type;
        this.styleArch = styleArch;
        this.budget = budget;
        this.etat = etat;
        this.dateCreation = dateCreation;
    }

    public Projet(int Id_projet, String nomProjet, int Id_equipe, int id_client, BigDecimal budget, String type, String styleArch, String etat, Timestamp dateCreation) {
        this.Id_projet = Id_projet;
        this.nomProjet = nomProjet;
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.etat = etat;
        this.dateCreation = dateCreation;
    }

    public int getId_projet() {
        return Id_projet;
    }

    public void setId_projet(int id_projet) {
        Id_projet = id_projet;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public int getId_equipe() {
        return Id_equipe;
    }

    public void setId_equipe(int id_equipe) {
        Id_equipe = id_equipe;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyleArch() {
        return styleArch;
    }

    public void setStyleArch(String styleArch) {
        this.styleArch = styleArch;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "Id_projet=" + Id_projet +
                ", nomProjet='" + nomProjet + '\'' +
                ", Id_equipe=" + Id_equipe +
                ", id_client=" + id_client +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", styleArch='" + styleArch + '\'' +
                ", etat='" + etat + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
