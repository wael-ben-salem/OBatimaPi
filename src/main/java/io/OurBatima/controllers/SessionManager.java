package io.OurBatima.controllers;

import io.OurBatima.core.model.Utilisateur;

public class SessionManager {
    private static Utilisateur currentUser;

    private static Utilisateur utilisateur;

    public static void setUtilisateur(Utilisateur user) {
        currentUser = user;
    }


    public static Utilisateur getUtilisateur() {
        return currentUser;
    }}
