package io.ourbatima.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private int Id_terrain;
    private String emplacement;
    private String caracteristiques;
    private BigDecimal superficie;
    private String detailsGeo;
    private List<Visite> visites;
    private List<String> observations;

    public Terrain(){}


    public Terrain(String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement) {
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
    }

    public Terrain(int Id_terrain, String detailsGeo, String caracteristiques, BigDecimal superficie, String emplacement) {
        Id_terrain = Id_terrain;
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
    }

    public Terrain(int Id_terrain, String emplacement){
        this.Id_terrain=Id_terrain;
        this.emplacement=emplacement;
    }

    public Terrain(int Id_terrain, String detailsGeo, BigDecimal superficie, String caracteristiques, String emplacement) {
        this.Id_terrain = Id_terrain;
        this.detailsGeo = detailsGeo;
        this.superficie = superficie;
        this.caracteristiques = caracteristiques;
        this.emplacement = emplacement;
        this.observations = (observations != null) ? observations : new ArrayList<>();
    }



    public Terrain(String emplacement, BigDecimal superficie) {
        this.superficie = superficie;
        this.emplacement=emplacement;
    }

    public List<String> getObservations() {
        return observations;
    }
    public void setObservations(List<String> observations) {
        this.observations = observations;
    }

    public Terrain(String emplacement) {
        this.emplacement = emplacement;
    }

    public void setVisites(List<Visite> visites) { this.visites = visites; }

    public Terrain(int Id_terrain) {
        this.Id_terrain = Id_terrain;
    }

    public int getId_terrain() {
        return Id_terrain;
    }

    public List<Visite> getVisites() {
        return visites;
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


    @Override
    public String toString() {
        return "Terrain{" +
                "Id_terrain=" + Id_terrain +
                ", emplacement='" + emplacement + '\'' +
                ", caracteristiques='" + caracteristiques + '\'' +
                ", superficie=" + superficie +
                ", detailsGeo='" + detailsGeo + '\'' +
                ", observations=" + observations + '\''+
                '}';
    }


}
