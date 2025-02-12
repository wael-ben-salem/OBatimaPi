package io.OurBatima.core.model.financeModel;


import java.util.Date;

public class Contrat {
    private int idContrat;
    private String typeContrat;
    private Date dateSignature;
    private Date dateDebut;
    private String signatureElectronique;
    private Date dateFin;
    private double montantTotal;
    private int idClient;
    private int idProjet;
    private int idConstructeur;

    // Constructeur
    public Contrat(int idContrat, String typeContrat, Date dateSignature, Date dateDebut, String signatureElectronique,
                   Date dateFin, double montantTotal, int idClient, int idProjet, int idConstructeur) {
        this.idContrat = idContrat;
        this.typeContrat = typeContrat;
        this.dateSignature = dateSignature;
        this.dateDebut = dateDebut;
        this.signatureElectronique = signatureElectronique;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idProjet = idProjet;
        this.idConstructeur = idConstructeur;
    }


    public Contrat(int idContrat, String typeContrat, Date dateSignature, Date dateDebut, String signatureElectronique,
                   Date dateFin, double montantTotal, int idClient, int idProjet) {
        this.idContrat = idContrat;
        this.typeContrat = typeContrat;
        this.dateSignature = dateSignature;
        this.dateDebut = dateDebut;
        this.signatureElectronique = signatureElectronique;
        this.dateFin = dateFin;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.idProjet = idProjet;

    }


    public Contrat() {

    }

    // Getters et Setters
    public int getIdContrat() {
        return idContrat;
    }

    public void setIdContrat(int idContrat) {
        this.idContrat = idContrat;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public Date getDateSignature() {
        return dateSignature;
    }

    public void setDateSignature(Date dateSignature) {
        this.dateSignature = dateSignature;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String isSignatureElectronique() {
        return signatureElectronique;
    }

    public void setSignatureElectronique(String signatureElectronique) {
        this.signatureElectronique = signatureElectronique;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public int getIdConstructeur() {
        return idConstructeur;
    }

    public void setIdConstructeur(int idConstructeur) {
        this.idConstructeur = idConstructeur;
    }

    @Override
    public String toString() {
        return "Contrat{" +
                "idContrat=" + idContrat +
                ", typeContrat='" + typeContrat + '\'' +
                ", dateSignature=" + dateSignature +
                ", dateDebut=" + dateDebut +
                ", signatureElectronique=" + signatureElectronique +
                ", dateFin=" + dateFin +
                ", montantTotal=" + montantTotal +
                ", idClient=" + idClient +
                ", idProjet=" + idProjet +
                ", idConstructeur=" + idConstructeur +
                '}';
    }
}

