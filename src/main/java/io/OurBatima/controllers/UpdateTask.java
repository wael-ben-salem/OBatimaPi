package io.ourbatima.controllers;

import io.ourbatima.core.Dao.taches.TacheDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Tache;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class UpdateTask extends ActionView {

    @FXML private TextField descriptionField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private ComboBox<Utilisateur> utilisateursArtisans;
    @FXML private ComboBox<Utilisateur> utilisateursConstructeurs;

    private int taskId;
    private final TacheDAO tacheDAO = new TacheDAO();

    public void setTaskId(int id) {
        this.taskId = id;
        loadTaskData();
    }

    private void loadTaskData() {
        Tache task = tacheDAO.getTaskById(taskId);
        if (task != null) {
            descriptionField.setText(task.getDescription());
            dateDebutPicker.setValue(task.getDateDebut().toLocalDate());
            if (task.getDateFin() != null) {
                dateFinPicker.setValue(task.getDateFin().toLocalDate());
            }
            etatComboBox.setValue(task.getEtat());

            // Load artisans and constructeurs
            utilisateursArtisans.setItems(FXCollections.observableArrayList(getUsersFromDatabase("Artisan")));
            utilisateursConstructeurs.setItems(FXCollections.observableArrayList(getUsersFromDatabase("Constructeur")));

            // Set selected values
            utilisateursArtisans.setValue(findUserById(utilisateursArtisans.getItems(), task.getArtisanId()));
            utilisateursConstructeurs.setValue(findUserById(utilisateursConstructeurs.getItems(), task.getConstructeurId()));
        }
    }

    @FXML
    private void updateTask() {
        String description = descriptionField.getText();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        String etat = etatComboBox.getValue();
        Utilisateur selectedArtisan = utilisateursArtisans.getValue();
        Utilisateur selectedConstructeur = utilisateursConstructeurs.getValue();

        if (description.isEmpty() || dateDebut == null) {
            System.out.println("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Create updated task without modifying `Id_projet`
        Tache updatedTask = new Tache(
                taskId,
                0, // Keep Id_projet unchanged
                selectedConstructeur != null ? selectedConstructeur.getId() : null,
                selectedArtisan != null ? selectedArtisan.getId() : null,
                description,
                Date.valueOf(dateDebut),
                dateFin != null ? Date.valueOf(dateFin) : null,
                etat
        );

        tacheDAO.modifierTache(updatedTask);

        ((Stage) descriptionField.getScene().getWindow()).close();
    }

    public void closePopup(ActionEvent actionEvent) {
        ((Stage) descriptionField.getScene().getWindow()).close();
    }

    private List<Utilisateur> getUsersFromDatabase(String role) {
        return new AddTask().getUsersFromDatabase(role);
    }

    private Utilisateur findUserById(List<Utilisateur> users, Integer id) {
        if (id == null) return null;
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
}
