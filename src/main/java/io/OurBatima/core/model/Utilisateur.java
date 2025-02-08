package io.OurBatima.core.model;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;
    private String statut = "désactivé";
    private boolean isConfirmed = false;
    private String role = "USER";

    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse, String telephone, String adresse, String statut, boolean isConfirmed, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.statut = statut;
        this.isConfirmed = isConfirmed;
        this.role = role;

    }

    public Utilisateur() {

    }

    // Getters et setters
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

    public String getStatut() { return statut; }
    public void setStatut(String statut) {
        if ("activé".equals(statut) || "désactivé".equals(statut)) {
            this.statut = statut;
        } else {
            throw new IllegalArgumentException("Statut invalide. Les valeurs possibles sont 'activé' ou 'désactivé'.");
        }
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if ("ADMIN".equals(role) || "USER".equals(role)) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Rôle invalide. Les valeurs possibles sont 'ADMIN' ou 'USER'.");
        }
    }
    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean isConfirmed) { this.isConfirmed = isConfirmed; }

}
