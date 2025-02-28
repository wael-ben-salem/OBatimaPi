package io.ourbatima.controllers;

import io.ourbatima.core.model.Utilisateur.Utilisateur;

import java.util.logging.Logger;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur currentUser;

    // Rendre le constructeur privé
    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    private static final Logger logger = Logger.getLogger(SessionManager.class.getName());

    public void startSession(Utilisateur user) {
        if(user == null ) {
            logger.severe("Tentative de session invalide : " + user);
            throw new IllegalArgumentException("Utilisateur invalide");
        }

        this.currentUser = user;
        logger.info("Nouvelle session : " + user.getEmail());
    }

    public static Utilisateur getUtilisateur() {
        return getInstance().currentUser;
    }

    public static void clearSession() {
        getInstance().currentUser = null;
    }
}