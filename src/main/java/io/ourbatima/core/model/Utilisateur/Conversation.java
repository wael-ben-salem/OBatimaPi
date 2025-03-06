package io.ourbatima.core.model.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;

public class Conversation {
    private int id;
    private int equipeId;
    private String nom;
    private LocalDateTime dateCreation;
    private List<Integer> membres;
    private List<Message> messages;

    // Constructeurs
    public Conversation() {}

    public Conversation(int equipeId, String nom) {
        this.equipeId = equipeId;
        this.nom = nom;
        this.dateCreation = LocalDateTime.now();
    }

    public Conversation(int referenceId) {

    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEquipeId() { return equipeId; }
    public void setEquipeId(int equipeId) { this.equipeId = equipeId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public List<Integer> getMembres() { return membres; }
    public void setMembres(List<Integer> membres) { this.membres = membres; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}