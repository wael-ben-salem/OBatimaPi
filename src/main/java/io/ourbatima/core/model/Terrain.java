package io.ourbatima.core.model;

import java.math.BigDecimal;

public class Terrain {
    private int Id_terrain;
    private Integer Id_projet;
    private String emplacement;
    private String caracteristiques;
    private BigDecimal superficie;
    private String detailsGeo;
    private Integer Id_visite;

    public Terrain(){}

    public Terrain(Integer Id_projet, String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement, Integer Id_visite) {
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
        this.Id_projet = Id_projet;
        this.Id_visite = Id_visite;
    }

    public Terrain(int id_terrain, Integer Id_projet, String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement, Integer Id_visite) {
        Id_terrain = id_terrain;
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
        this.Id_projet = Id_projet;
        this.Id_visite = Id_visite;
    }

    public Terrain(String emplacement) {
        this.emplacement = emplacement;
    }


    public Terrain(int Id_terrain) {
        this.Id_terrain = Id_terrain;
    }

    public void setId_projet(Integer id_projet) {
        Id_projet = id_projet;
    }

    public Integer getId_projet() {
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
        if (superficie != null && superficie.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Superficie cannot be negative");
        }
        this.superficie = superficie;
    }

    public void setCaracteristiques(String caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    public Integer getId_visite() { return Id_visite; }

    public void setId_visite(Integer id_visite) { this.Id_visite = id_visite; }

    @Override
    public String toString() {
        return "Terrain{" +
                "Id_terrain=" + Id_terrain +
                ", Id_projet=" + Id_projet +
                ", emplacement='" + emplacement + '\'' +
                ", caracteristiques='" + caracteristiques + '\'' +
                ", superficie=" + superficie +
                ", detailsGeo='" + detailsGeo + '\'' +
                ", Id_visite=" + Id_visite +
                '}';
    }


}
