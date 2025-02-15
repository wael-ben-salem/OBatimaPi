package io.ourbatima.core.model.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;

public class Utilisateur {
    private int id;
    private String nom;

    public GestionnaireDeStock getGestionnaireDeStock() {
        return gestionnaireDeStock;
    }

    public void setGestionnaireDeStock(GestionnaireDeStock gestionnaireDeStock) {
        this.gestionnaireDeStock = gestionnaireDeStock;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private Artisan artisan;

    private GestionnaireDeStock gestionnaireDeStock;

    private Client client;
    private Constructeur constructeur;

    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;
    private Statut statut = Statut.en_attente; // Valeur par défaut
    private boolean isConfirmed = false; // Valeur par défaut
    private Role role = Role.Client; // Valeur par défaut

    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, UtilisateurDAO.Statut statut, boolean isConfirmed, UtilisateurDAO.Role role) {

    }

    // Enumérations
    public enum Role {
        Artisan, Constructeur, GestionnaireStock, Admin, Client
    }

    public enum Statut {
        actif, inactif, en_attente
    }

    // Constructeur complet
    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, Statut statut, boolean isConfirmed, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.statut = statut != null ? statut : Statut.en_attente; // Valeur par défaut si null
        this.isConfirmed = isConfirmed;
        this.role = role != null ? role : Role.Client; // Valeur par défaut si null
    }

    // Constructeur vide
    public Utilisateur() {
        // Initialisation des valeurs par défaut
        this.statut = Statut.en_attente;
        this.role = Role.Client;
        this.isConfirmed = false;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) {
        this.statut = statut != null ? statut : Statut.en_attente; // Valeur par défaut si null
    }

    public Role getRole() { return role; }
    public void setRole(Role role) {
        this.role = role != null ? role : Role.Client; // Valeur par défaut si null
    }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean isConfirmed) { this.isConfirmed = isConfirmed; }


    // Méthode toString pour le débogage
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", statut=" + statut +
                ", isConfirmed=" + isConfirmed +
                ", role=" + role +
                '}';
    }

    public Artisan getArtisan() {
        return artisan;
    }

    public void setArtisan(Artisan artisan) {
        this.artisan = artisan;
    }
    public Constructeur getConstructeur() {
        return constructeur;
    }

    public void setConstructeur(Constructeur constructeur) {
        this.constructeur = constructeur;
    }

}