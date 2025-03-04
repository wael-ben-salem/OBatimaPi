package io.ourbatima.core.model.Utilisateur;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DirectMessage {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    // Constructeur complet
    public DirectMessage(int messageId, int senderId, int receiverId,
                         String content, LocalDateTime sentAt, boolean isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    // Constructeur pour nouvelle création
    public DirectMessage(int senderId, int receiverId, String content) {
        this(0, senderId, receiverId, content, LocalDateTime.now(), false);
    }

    // Getters
    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    // Setters
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    // Méthode utilitaire
    public String getFormattedTimestamp() {
        return sentAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Message #%d [De: %d, Pour: %d] - %s",
                messageId, senderId, receiverId, getFormattedTimestamp());
    }
}