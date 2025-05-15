package io.ourbatima.controllers;

import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.services.ProfanityFilterService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class AddTask extends ActionView {

    @FXML
    private ComboBox<Utilisateur> utilisateursArtisans;

    @FXML
    private ComboBox<Utilisateur> utilisateursConstructeurs;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    public void initialize() {
        // No need to populate etatComboBox as it was removed
    }

    @FXML
    public void showArtisans() {
        List<Utilisateur> artisans = getUsersFromDatabase("Artisan");
        utilisateursArtisans.setItems(FXCollections.observableArrayList(artisans));
        utilisateursArtisans.setVisible(true);
    }

    @FXML
    public void showConstructeurs() {
        List<Utilisateur> constructeurs = getUsersFromDatabase("Constructeur");
        utilisateursConstructeurs.setItems(FXCollections.observableArrayList(constructeurs));
        utilisateursConstructeurs.setVisible(true);
    }

    public List<Utilisateur> getUsersFromDatabase(String role) {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT id, nom FROM Utilisateur WHERE role = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nom");
                users.add(new Utilisateur(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private void showErrorPopup() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Saisie 🏗️");
        alert.setHeaderText("Champs Obligatoires Manquants 🚧");
        alert.setContentText("Veuillez remplir tous les champs requis :\n"
                + "✅ Description de la tâche\n"
                + "✅ Date de début\n"
                + "🏗️ Génie Civil - Construisons mieux ensemble !");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffe4b5;");
        alert.showAndWait();
    }

    // Overloaded method for custom messages
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void saveTask() {
        Utilisateur selectedArtisan = utilisateursArtisans.getValue();
        String description = descriptionField.getText();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        // Get current user
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) {
            showErrorPopup("Vous devez être connecté pour créer une tâche.");
            return;
        }

        // Validate required fields
        if (description == null || description.trim().isEmpty() || dateDebut == null) {
            showErrorPopup("Erreur lors de l'ajout de la tâche.");
            return;
        }



        int constructeurId = currentUser.getId();

        // Filter profanity
        String censoredDescription = ProfanityFilterService.filterProfanity(description);

        String query = "INSERT INTO tache (artisan_id, constructeur_id, description, date_debut, date_fin) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, selectedArtisan != null ? selectedArtisan.getId() : null);
            stmt.setInt(2, constructeurId); // Current user's ID as constructeur_id
            stmt.setString(3, censoredDescription);
            stmt.setDate(4, java.sql.Date.valueOf(dateDebut));
            stmt.setObject(5, dateFin != null ? java.sql.Date.valueOf(dateFin) : null);

            stmt.executeUpdate();
            showSuccessPopup("Tâche ajoutée avec succès ! ✅🏗️🚀");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorPopup("Erreur lors de l'ajout de la tâche.");
        }
    }

    private void showSuccessPopup(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès ✅");
        alert.setHeaderText("Tâche ajoutée 🏢🎉");
        alert.setContentText(s);
        alert.showAndWait();
    }

    public void seeAllTasks(ActionEvent actionEvent) {
        context.routes().nav("ListTaches");
    }


}