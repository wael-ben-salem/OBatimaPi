package io.ourbatima.controllers;

import io.ourbatima.core.model.Utilisateur.Utilisateur;

public class SessionManager {
    private static Utilisateur currentUser;

    private static Utilisateur utilisateur;

    public static void setUtilisateur(Utilisateur user) {
        currentUser = user;
    }


    public static Utilisateur getUtilisateur() {
        return currentUser;
    }}
