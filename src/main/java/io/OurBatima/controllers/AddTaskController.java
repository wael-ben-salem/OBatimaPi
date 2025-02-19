package io.ourbatima.controllers;

import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.stream.Collectors;

public class AddTaskController extends ActionView {

    @FXML
    private ComboBox<String> userComboBox;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        System.out.println("✅ AddTaskController is being initialized!");

        if (userComboBox == null) {
            System.out.println("❌ ERROR: userComboBox is NULL! Check your FXML fx:id.");
            return; // Stop execution if the ComboBox is null
        }

        List<String> users = getUsersFromDatabase();
        System.out.println("📋 Users retrieved from database: " + users);

        if (users.isEmpty()) {
            System.out.println("⚠️ WARNING: No users found in database!");
        } else {
            userComboBox.setItems(FXCollections.observableArrayList(users));
            System.out.println("✅ ComboBox updated successfully with " + users.size() + " users.");
        }
    }

    public List<String> getUsersFromDatabase() {
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllArtisant(); // Ensure this method works

        if (utilisateurs == null) {
            System.out.println("❌ ERROR: getAllArtisants() returned null!");
            return List.of();
        }

        System.out.println("📊 Retrieved " + utilisateurs.size() + " users from database.");
        return utilisateurs.stream()
                .map(Utilisateur::getNom)
                .collect(Collectors.toList());
    }
}
