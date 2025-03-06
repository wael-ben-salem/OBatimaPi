package io.ourbatima.controllers;

import io.ourbatima.core.Dao.plannification.PlannificationDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Plannification;
import io.ourbatima.core.model.Tache;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class AddPlan extends ActionView {

    @FXML
    private ComboBox<Tache> tacheDescriptions;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField heureDebutField;
    @FXML
    private TextField heureFinField;
    @FXML
    private TextArea remarquesArea;
    @FXML
    private Button seeTachesButton;
    @FXML
    private Button editTaskButton;
    @FXML
    private Button saveButton;

    private final PlannificationDAO plannificationDAO = new PlannificationDAO();

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    public void initialize() {
        editTaskButton.setDisable(true);
        tacheDescriptions.setOnAction(event -> editTaskButton.setDisable(tacheDescriptions.getValue() == null));

        // Populate the statusComboBox
        statusComboBox.setItems(FXCollections.observableArrayList("Planifié", "En cours", "Terminé"));
    }

    @FXML
    public void onSeeTachesClicked() {
        List<Tache> taches = getTachesFromDatabase();
        if (!taches.isEmpty()) {
            tacheDescriptions.setItems(FXCollections.observableArrayList(taches));
            tacheDescriptions.setVisible(true);
        }
    }

    @FXML
    public void onEditTaskClicked() {
        Tache selectedTask = tacheDescriptions.getValue();
        if (selectedTask != null) {
            openUpdatePopup(selectedTask.getIdTache());
        } else {
            System.out.println("No task selected!");
        }
    }

    private List<Tache> getTachesFromDatabase() {
        List<Tache> taches = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_tache, description FROM tache");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_tache");
                String description = rs.getString("description");
                taches.add(new Tache(id, description));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return taches;
    }

    private void openUpdatePopup(int idTache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/update_task.fxml"));
            Parent root = loader.load();

            UpdateTask controller = loader.getController();
            controller.setTaskId(idTache);

            Stage stage = new Stage();
            stage.setTitle("Update Task");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSaveClicked() {
        Tache selectedTache = tacheDescriptions.getValue();
        if (selectedTache == null || datePicker.getValue() == null || heureDebutField.getText().isEmpty() || heureFinField.getText().isEmpty()) {
            showValidationError("Tous les champs sont obligatoires ! 🚧🏗️🏢\n\nVeuillez sélectionner une tâche, une date, une heure de début et une heure de fin.");
            return;
        }

        // Validate time format
        Time heureDebut = null;
        Time heureFin = null;
        try {
            heureDebut = Time.valueOf(LocalTime.parse(heureDebutField.getText()));
            heureFin = Time.valueOf(LocalTime.parse(heureFinField.getText()));
        } catch (Exception e) {
            showValidationError("Format d'heure invalide ! 🕐⚠️\n\nUtilisez le format HH:mm:ss.");
            return;
        }

        // Get selected status
        String selectedStatus = statusComboBox.getValue();

        Plannification newPlan = new Plannification(
                selectedTache.getIdTache(),
                Date.valueOf(datePicker.getValue()),
                heureDebut,
                heureFin,
                remarquesArea.getText(),
                selectedStatus
        );

        plannificationDAO.ajouterPlannification(newPlan);

        // Send emails to the users related to the task
        sendEmailsToUsers(selectedTache.getIdTache());

        showSuccessPopup("Plannification ajoutée avec succès ! ✅🏗️🚀");
    }

    private void sendEmailsToUsers(int taskId) {
        new Thread(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT u.email, u.nom, u.role FROM Utilisateur u JOIN Tache t ON u.id = t.constructeur_id OR u.id = t.artisan_id WHERE t.id_tache = ?")) {

                stmt.setInt(1, taskId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String email = rs.getString("email");
                    String name = rs.getString("nom");
                    String role = rs.getString("role");

                    if (email != null && !email.isEmpty()) {
                        String subject = "Nouvelle planification de tâche";
                        String message = "Bonjour " + name + ",\n\n" +
                                "Une nouvelle planification a été ajoutée pour une tâche associée à votre rôle (" + role + ").\n" +
                                "Veuillez vérifier les détails dans votre espace personnel.\n\n" +
                                "Cordialement,\n" +
                                "OBATIMA";

                        EmailService.sendEmail(email, subject, message);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Saisie 🚧");
        alert.setHeaderText("Validation des Champs Manquants 🏗️");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès ✅");
        alert.setHeaderText("Tâche Planifiée 🏢🎉");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSeeAllPlansClicked(ActionEvent actionEvent) {
        context.routes().nav("ListPlan");
    }
}
