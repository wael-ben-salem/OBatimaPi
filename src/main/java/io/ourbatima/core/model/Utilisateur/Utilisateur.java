
package io.ourbatima.core.model.Utilisateur;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

public class Utilisateur {
    private int id;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    private String nom;

    public Utilisateur(String nom, String prenom, String email, String telephone, String adresse, String motDePasse ,Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.motDePasse = motDePasse;
        this.role = role;

    }


    public Utilisateur(int id, String nom, String prenom, String email, String telephone, String adresse, Statut actif, boolean b, Role role) {

    }


    public Utilisateur(int id, String name) {
        this.id = id;
        this.nom = name;//mahdiiiii
    }

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

    private byte[] faceData;

    private String agoraUid;
    private String agoraToken;

    private Statut statut = Statut.en_attente; // Valeur par défaut
    private boolean isConfirmed = false; // Valeur par défaut
    private Role role = Role.Client; // Valeur par défaut

    public byte[] getFaceData() { return faceData; }
    public void setFaceData(byte[] faceData) { this.faceData = faceData; }
    public boolean hasFaceData() {
        return faceData != null && faceData.length > 0;
    }
    public Image getFaceImage() {
        if (faceData != null) {
            return new Image(new ByteArrayInputStream(faceData));
        }
        return null;
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


    //mahdiiiii
    @Override
    public String toString() {
        return nom;  // Pour affichage dans ComboBox
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
    public String getAgoraUid() { return agoraUid; }
    public void setAgoraUid(String agoraUid) { this.agoraUid = agoraUid; }
    public String getAgoraToken() { return agoraToken; }
    public void setAgoraToken(String agoraToken) { this.agoraToken = agoraToken; }

}