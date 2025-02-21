package io.ourbatima.core.model.financeModel;


import javafx.beans.property.*;

import java.sql.Date;

public class ContratDTO {

        private final IntegerProperty idContrat;
        private final StringProperty typeContrat;
        private final ObjectProperty<Date> dateSignature;
        private final ObjectProperty<Date> dateDebut;
        private final StringProperty signatureElectronique;
        private final ObjectProperty<Date> dateFin;
        private final DoubleProperty montantTotal;
        private final IntegerProperty idProjet;
        private final StringProperty nomClient;
        private final StringProperty nomProjet;

        public ContratDTO(int idContrat, String typeContrat, Date dateSignature, Date dateDebut,
                          String signatureElectronique, Date dateFin, double montantTotal,
                          int idProjet, String nomClient, String nomProjet) {
            this.idContrat = new SimpleIntegerProperty(idContrat);
            this.typeContrat = new SimpleStringProperty(typeContrat);
            this.dateSignature = new SimpleObjectProperty<>(dateSignature);
            this.dateDebut = new SimpleObjectProperty<>(dateDebut);
            this.signatureElectronique = new SimpleStringProperty(signatureElectronique);
            this.dateFin = new SimpleObjectProperty<>(dateFin);
            this.montantTotal = new SimpleDoubleProperty(montantTotal);
            this.idProjet = new SimpleIntegerProperty(idProjet);
            this.nomClient = new SimpleStringProperty(nomClient);
            this.nomProjet = new SimpleStringProperty(nomProjet);
        }

        // Getters for properties
        public IntegerProperty idContratProperty() { return idContrat; }
        public StringProperty typeContratProperty() { return typeContrat; }
        public ObjectProperty<Date> dateSignatureProperty() { return dateSignature; }
        public ObjectProperty<Date> dateDebutProperty() { return dateDebut; }
        public StringProperty signatureElectroniqueProperty() { return signatureElectronique; }
        public ObjectProperty<Date> dateFinProperty() { return dateFin; }
        public DoubleProperty montantTotalProperty() { return montantTotal; }
        public IntegerProperty idProjetProperty() { return idProjet; }
        public StringProperty nomClientProperty() { return nomClient; }
        public StringProperty nomProjetProperty() { return nomProjet; }

        // Regular getters (optional, but useful for non-JavaFX code)
        public int getIdContrat() { return idContrat.get(); }
        public String getTypeContrat() { return typeContrat.get(); }
        public Date getDateSignature() { return dateSignature.get(); }
        public Date getDateDebut() { return dateDebut.get(); }
        public String isSignatureElectronique() { return signatureElectronique.get(); }
        public Date getDateFin() { return dateFin.get(); }
        public double getMontantTotal() { return montantTotal.get(); }
        public int getIdProjet() { return idProjet.get(); }
        public String getNomClient() { return nomClient.get(); }
        public String getNomProjet() { return nomProjet.get(); }

    @Override
    public String toString() {
        return "ContratDTO{" +
                "idContrat=" + idContrat.get() +
                ", typeContrat='" + typeContrat.get() + '\'' +
                ", dateSignature=" + dateSignature.get() +
                ", dateDebut=" + dateDebut.get() +
                ", signatureElectronique=" + signatureElectronique.get() +
                ", dateFin=" + dateFin.get() +
                ", montantTotal=" + montantTotal.get() +
                ", idProjet=" + idProjet.get() +
                ", nomClient='" + nomClient.get() + '\'' +
                ", nomProjet='" + nomProjet.get() + '\'' +
                '}';
    }


    }


