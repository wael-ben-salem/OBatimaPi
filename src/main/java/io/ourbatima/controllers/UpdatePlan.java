package io.ourbatima.controllers;

import io.ourbatima.core.Dao.plannification.PlannificationDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Plannification;
import io.ourbatima.core.model.Tache;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class UpdatePlan extends ActionView {

    @FXML
    private ComboBox<Tache> tacheDescriptions;
    @FXML
    private DatePicker datePlanifieePicker;
    @FXML
    private TextField heureDebutField;
    @FXML
    private TextField heureFinField;
    @FXML
    private TextArea remarquesField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final PlannificationDAO plannificationDAO = new PlannificationDAO();
    private Plannification planToUpdate;

    @FXML
    public void initialize() {
        loadTaches(); // Load tasks when the window opens
    }

    private void loadTaches() {
        List<Tache> taches = getTachesFromDatabase();
        tacheDescriptions.setItems(FXCollections.observableArrayList(taches));
    }

    public void setPlanToUpdate(Plannification plan) {
        this.planToUpdate = plan;
        loadTaches();

        if (plan != null) {
            tacheDescriptions.getSelectionModel().select(findTacheById(plan.getIdTache()));
            datePlanifieePicker.setValue(plan.getDatePlanifiee().toLocalDate());
            heureDebutField.setText(plan.getHeureDebut() != null ? plan.getHeureDebut().toString() : "");
            heureFinField.setText(plan.getHeureFin() != null ? plan.getHeureFin().toString() : "");
            remarquesField.setText(plan.getRemarques());
        }
    }

    @FXML
    public void updatePlan() {
        if (planToUpdate == null || tacheDescriptions.getValue() == null || datePlanifieePicker.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        try {
            Time heureDebut = heureDebutField.getText().isEmpty() ? null : Time.valueOf(LocalTime.parse(heureDebutField.getText()));
            Time heureFin = heureFinField.getText().isEmpty() ? null : Time.valueOf(LocalTime.parse(heureFinField.getText()));

            Plannification updatedPlan = new Plannification(
                    planToUpdate.getIdPlannification(),
                    tacheDescriptions.getValue().getIdTache(),
                    Date.valueOf(datePlanifieePicker.getValue()),
                    heureDebut,
                    heureFin,
                    remarquesField.getText()
            );

            plannificationDAO.updatePlannification(updatedPlan);
            System.out.println("✅ Plan mis à jour avec succès !");
            closePopup();
        } catch (Exception e) {
            System.out.println("❌ ERROR: Format incorrect !");
            e.printStackTrace();
        }
    }

    @FXML
    public void closePopup() {
        Stage stage = (Stage) datePlanifieePicker.getScene().getWindow();
        stage.close();
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
            e.printStackTrace();
        }
        return taches;
    }

    private Tache findTacheById(int idTache) {
        return tacheDescriptions.getItems().stream()
                .filter(t -> t.getIdTache() == idTache)
                .findFirst()
                .orElse(null);
    }

    public void setPlanId(int idPlan) {
        Plannification plan = plannificationDAO.getPlannificationById(idPlan);
        setPlanToUpdate(plan);
    }
}
