package io.ourbatima.core.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.Client;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Utilisateur.Utilisateur;

public class Projet {
    private int Id_projet;
    private String nomProjet;
    private Integer Id_equipe;
    private Client client;
    private int id_client;
    private BigDecimal budget;
    private String type;
    private String styleArch;
    private String etat;
    private Timestamp dateCreation;
    private List<EtapeProjet> etapes;
    private Terrain terrain;
    private int id_terrain;

    public Projet() {
        this.etapes = null;
        this.terrain = new Terrain();
        this.client = null;
    }


    public Projet(String nomProjet, int Id_equipe, int id_client, int id_terrain, BigDecimal budget, String type, String styleArch, Timestamp dateCreation) {
        this.nomProjet = nomProjet;
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.id_terrain = id_terrain;
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.dateCreation = dateCreation;
    }


    public Projet(int Id_projet, String nomProjet, Integer Id_equipe, int id_client, List<EtapeProjet> etapes, BigDecimal budget, Terrain terrain, String type, String styleArch, String etat, Timestamp dateCreation) {
        this.Id_projet = Id_projet;
        this.nomProjet = nomProjet;
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.etat = (etat != null && !etat.isEmpty()) ? etat : "En attente";
        this.dateCreation = dateCreation;
        this.etapes = (etapes != null) ? etapes : new ArrayList<>();
        this.terrain = (terrain != null) ? terrain : new Terrain();
    }


    public Projet(int Id_projet, String type, String styleArch, BigDecimal budget, String etat, Timestamp dateCreation, String terrainEmplacement, int equipeId, int clientId, List<EtapeProjet> etapes) {
        this.Id_projet = Id_projet;
        this.type = type;
        this.styleArch = styleArch;
        this.budget = budget;
        this.etat = (etat != null && !etat.isEmpty()) ? etat : "En attente";
        this.dateCreation = dateCreation;
        this.etapes = (etapes != null) ? etapes : new ArrayList<>();
        this.terrain = new Terrain(terrainEmplacement);
        this.Id_equipe = equipeId;
        this.id_client = clientId;
    }


    public Projet(String nomProjet, Integer Id_equipe, int id_client, int id_terrain, BigDecimal budget, String type, String styleArch, String etat,Timestamp dateCreation) {
        this.nomProjet = nomProjet;
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.budget = budget;
        this.type = type;
        this.styleArch = styleArch;
        this.dateCreation = dateCreation;
        this.etat = etat;
        this.id_terrain = id_terrain;
    }

    public Projet(int Id_projet, String nomProjet, String type, String styleArch, BigDecimal budget, String etat, Timestamp dateCreation, int id_terrain, int Id_equipe, int id_client, List<EtapeProjet> etapes) {
        this.Id_projet = Id_projet;
        this.nomProjet = nomProjet;
        this.type = type;
        this.styleArch = styleArch;
        this.budget = budget;
        this.etat = etat;
        this.dateCreation = dateCreation;
        this.id_terrain = id_terrain;
        this.Id_equipe = Id_equipe;
        this.id_client = id_client;
        this.etapes = (etapes != null) ? etapes : new ArrayList<>();
    }



    public Projet(int id, String name) {
        this.Id_projet=id;
        this.nomProjet=name;
    }

    public String getEmailClientById() {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateur = utilisateurDAO.getUserProjById(id_client);
        Client client = null;

        if (utilisateur instanceof Client) {
            client = (Client) utilisateur;
        }

        return (client != null) ? client.getEmail() : null;
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

    public void setId_terrain(int id_terrain) {
        this.id_terrain = id_terrain;
    }

    public Integer getId_equipe() {
        return Id_equipe;
    }

    public void setId_equipe(Integer id_equipe) {
        Id_equipe = id_equipe;
    }


    public String getEmailClient() {
        return (client != null) ? client.getEmail() : "No client assigned";
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            this.id_client = client.getClient_id();
        }
    }

    public void setEmailClient(String email) {
        if (this.client != null) {
            this.client.setEmail(email);
        } else {
            System.out.println("No client assigned to this project.");
        }
    }

    public String getEmplacement() {
        if (id_terrain > 0) {
            return TerrainDAO.getTerrainEmplacementById(id_terrain);
        }
        return "N/A";
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

    public List<EtapeProjet> getEtapes() {
        return etapes;
    }

    public void setEtapes(List<EtapeProjet> etapes) {
        this.etapes = etapes;
    }

    public void addEtape(EtapeProjet etape) {
        if (this.etapes == null) {
            this.etapes = new ArrayList<>();
        }
        this.etapes.add(etape);
    }

    public void removeEtape(EtapeProjet etape) {
        if (this.etapes != null) {
            this.etapes.remove(etape);
        }
    }

    public int getId_terrain() {
        return id_terrain;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        if (terrain == null) {
            throw new IllegalArgumentException("Terrain cannot be null");
        }
        this.terrain = terrain;
    }
    public String getNomEquipe() {
        if (Id_equipe != null) {
            EquipeDAO equipeDAO = new EquipeDAO();
            return equipeDAO.getNomEquipeById(Id_equipe);
        }
        return "No team assigned";
    }

    @Override
    public String toString() {
        String etapesString = (etapes != null && !etapes.isEmpty())
                ? etapes.stream().map(EtapeProjet::getNomEtape).toList().toString()
                : "No Etapes assigned";

        return "Projet{" +
                "Id_projet=" + Id_projet +
                ", nomProjet='" + nomProjet + '\'' +
                ", Id_equipe=" + Id_equipe +
                ", id_client=" + id_client +
                ", emailClient='" + client + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", styleArch='" + styleArch + '\'' +
                ", etat='" + etat + '\'' +
                ", dateCreation=" + dateCreation +
                ", etapes=" + etapesString +
                ", Id_terrain=" + (terrain != null ? terrain.getId_terrain() : "No terrain assigned") +
                '}';
    }
}
