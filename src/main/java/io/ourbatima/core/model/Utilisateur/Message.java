package io.ourbatima.core.model.Utilisateur;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int conversationId;
    private int expediteurId;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu;

    // Constructeurs
    public Message() {}

    public Message(int conversationId, int expediteurId, String contenu) {
        this.conversationId = conversationId;
        this.expediteurId = expediteurId;
        this.contenu = contenu;
        this.dateEnvoi = LocalDateTime.now();
        this.lu = false;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }

    public int getExpediteurId() { return expediteurId; }
    public void setExpediteurId(int expediteurId) { this.expediteurId = expediteurId; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public void marquerCommeLu() { this.lu = true; }
}