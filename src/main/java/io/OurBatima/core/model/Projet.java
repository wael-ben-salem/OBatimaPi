package io.OurBatima.core.model;

import java.math.BigDecimal;
import java.sql.Date; // Import required for Date

public class Projet {
    private int Id_projet;
    private int Id_equipe;
    private BigDecimal budget;
    private String type;
    private String styleArch;
    private String etat;
    private Date dateCreation;


    public Projet(){}
    public Projet(int Id_equipe, String type, String styleArch, BigDecimal budget, String etat, Date dateCreation){
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.Id_equipe = Id_equipe;
    }
    public Projet(int Id_projet, int Id_equipe, BigDecimal budget, String type, String styleArch, String etat, Date dateCreation){
        this.Id_projet = Id_projet;
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.Id_equipe = Id_equipe;
    }

    public int getId_equipe() {
        return Id_equipe;
    }

    public void setId_equipe(int id_equipe) {
        Id_equipe = id_equipe;
    }

    public void setId_projet(int id_projet) {
        Id_projet = id_projet;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public void setStyleArch(String styleArch) {
        this.styleArch = styleArch;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getId_projet() {
        return Id_projet;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getType() {
        return type;
    }

    public String getStyleArch() {
        return styleArch;
    }

    public String getEtat() {
        return etat;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "Id_projet=" + Id_projet +
                "Id_equipe=" + Id_equipe +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", styleArch='" + styleArch + '\'' +
                ", etat='" + etat + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
