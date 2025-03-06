package io.ourbatima.core.model.Utilisateur;

import java.time.LocalDateTime;

public class Group {
    private int groupId;
    private int teamId;
    private String groupName;
    private LocalDateTime createdAt;

    public Group(int teamId, String groupName) {
        this.teamId = teamId;
        this.groupName = groupName;
        this.createdAt = LocalDateTime.now();
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}