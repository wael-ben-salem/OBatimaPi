package io.ourbatima.core.model;

import java.sql.Date;

public class Rapport {
    private int id;
    private int Id_etapeProjet;
    private String titre;
    private String contenu;
    private Date dateCreation;


    public Rapport(){}

    public Rapport(int Id_etapeProjet , String titre, String contenu, Date dateCreation) {
        this.titre = titre;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.Id_etapeProjet = Id_etapeProjet;
    }

    public Rapport(int id, int Id_etapeProjet , String titre, String contenu, Date dateCreation) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.Id_etapeProjet = Id_etapeProjet;
    }

    public int getId_etapeProjet() {
        return Id_etapeProjet;
    }

    public void setId_etapeProjet(int id_etapeProjet) {
        Id_etapeProjet = id_etapeProjet;
    }

    public int getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public String getTitre() {
        return titre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public String toString() {
        return "Rapport{" +
                "id=" + id +
                ", Id_etapeProjet='" + Id_etapeProjet + '\'' +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
