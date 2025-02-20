package io.OurBatima.core.model;

import java.time.LocalDateTime;

public class Reclamation {
    int id;
    String Description,statut;
    LocalDateTime date;



//constructeur sans commentaire 
    public Reclamation(){}

// constructeur avec paramétres 
    public Reclamation(int id,String Description,String
        statut,LocalDateTime date) {
            this.id = id;
            this.Description = Description;
            this.statut = statut;;
            this.date = date;
        }
// constructeur sans ID
    public Reclamation( String Description, String statut, LocalDateTime date) {

            this.Description = Description;
        this.statut = statut;
            this.date = date;
        }
        @Override
        public String toString() {
            return "Reclamation{" +
                    "id=" + id +
                    ", description=" + Description +
                    ", statut='" + statut + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

        public void setStatut(String Statut) {
                this.statut = statut;
        }
    public String getStatut() {
        return statut;
    }



    }

