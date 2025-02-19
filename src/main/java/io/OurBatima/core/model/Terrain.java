package io.ourbatima.core.model;

import java.math.BigDecimal;

public class Terrain {
    private int Id_terrain;
    private int Id_projet;
    private String emplacement;
    private String caracteristiques;
    private BigDecimal superficie;
    private String detailsGeo;

    public Terrain(){}

    public Terrain(int Id_projet, String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement) {
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
        this.Id_projet = Id_projet;
    }

    public Terrain(int id_terrain, int Id_projet, String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement) {
        Id_terrain = id_terrain;
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
        this.Id_projet = Id_projet;
    }

    public void setId_projet(int id_projet) {
        Id_projet = id_projet;
    }

    public int getId_projet() {
        return Id_projet;
    }

    public int getId_terrain() {
        return Id_terrain;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public String getCaracteristiques() {
        return caracteristiques;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public String getDetailsGeo() {
        return detailsGeo;
    }

    public void setId_terrain(int id_terrain) {
        Id_terrain = id_terrain;
    }

    public void setDetailsGeo(String detailsGeo) {
        this.detailsGeo = detailsGeo;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public void setCaracteristiques(String caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "Id_terrain=" + Id_terrain +
                ", Id_projet='" + Id_projet + '\'' +
                ", emplacement='" + emplacement + '\'' +
                ", caracteristiques='" + caracteristiques + '\'' +
                ", superficie='" + superficie + '\'' +
                ", detailsGeo='" + detailsGeo + '\'' +
                '}';
    }
}
