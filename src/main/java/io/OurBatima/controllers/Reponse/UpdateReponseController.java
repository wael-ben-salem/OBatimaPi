package io.OurBatima.controllers.Reponse;

import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UpdateReponseController extends ActionView {

    @FXML
    private TextArea descriptionAreaField;
    @FXML
    private ComboBox<String> statutComboBox;
    @FXML
    private ComboBox<Integer> reclamationIdComboBox;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final ReponseDAO reponseDAO = new ReponseDAO();
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();
    private Reponse reponseToUpdate;

    @FXML
    public void initialize() {
        // Load status options when the window opens
        System.out.println("UpdateReponseController Initialized");

        // Note: We don't need to set the action handlers here as they are already set in the FXML
        // with onAction="#updateReponse" and onAction="#closePopup"

        // Set default date to current date
        dateField.setValue(java.time.LocalDate.now());

        // Set default status
        statutComboBox.setValue("Pending");

        // Load reclamations into the ComboBox
        loadReclamations();

        // If the ComboBox is still empty, add some manual items
        if (reclamationIdComboBox.getItems().isEmpty()) {
            addManualReclamations();
        }
    }

    /**
     * Adds manual reclamation IDs to the ComboBox if database loading fails
     */
    private void addManualReclamations() {
        System.out.println("Adding manual reclamation IDs...");

        // Create a list of simple integer IDs (1-5)
        List<Integer> reclamationIds = new ArrayList<>();
        reclamationIds.add(1);
        reclamationIds.add(2);
        reclamationIds.add(3);
        reclamationIds.add(4);
        reclamationIds.add(5);

        // Add them to the ComboBox
        reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamationIds));

        System.out.println("Added " + reclamationIds.size() + " manual reclamation IDs");
    }

    private void loadReclamations() {
        try {
            System.out.println("Starting to load reclamation IDs...");

            // Get all reclamations from the database
            List<Reclamation> reclamations = reclamationDAO.getAllReclamations();

            // Check if we have any reclamations
            if (reclamations == null || reclamations.isEmpty()) {
                System.out.println("No reclamations found in database, using manual IDs");
                addManualReclamations();
                return;
            }

            // Clear existing items
            reclamationIdComboBox.getItems().clear();

            // Extract just the IDs from the reclamations
            List<Integer> reclamationIds = new ArrayList<>();
            for (Reclamation r : reclamations) {
                reclamationIds.add(r.getId());
                System.out.println("Added ID: " + r.getId());
            }

            // Add IDs to the ComboBox
            reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamationIds));

            // Debug output
            System.out.println("Loaded " + reclamationIds.size() + " reclamation IDs");
        } catch (Exception e) {
            System.out.println("ERROR loading reclamations: " + e.getMessage());
            e.printStackTrace();
            addManualReclamations();
        }
    }

    // Helper method to truncate long descriptions
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    public void setReponseToUpdate(Reponse reponse) {
        this.reponseToUpdate = reponse;

        if (reponse != null) {
            descriptionAreaField.setText(reponse.getDescription());
            statutComboBox.setValue(reponse.getStatut());
            dateField.setValue(reponse.getDate().toLocalDate());

            // Set the reclamation ID directly in the ComboBox
            reclamationIdComboBox.setValue(reponse.getIdReclamation());
            System.out.println("Set reclamation ID to: " + reponse.getIdReclamation());
        }
    }

    @FXML
    public void updateReponse() {
        System.out.println("updateReponse method called");

        if (descriptionAreaField.getText().isEmpty() ||
                statutComboBox.getValue() == null || dateField.getValue() == null || reclamationIdComboBox.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        if (reponseToUpdate == null) {
            System.out.println("❌ ERROR: No response to update!");
            return;
        }

        try {
            // Make sure we have a valid reclamation selected
            if (reclamationIdComboBox.getValue() == null) {
                System.out.println("❌ ERROR: Veuillez sélectionner une réclamation!");
                return;
            }

            // Debug output
            System.out.println("Updating response with ID: " + reponseToUpdate.getId());
            System.out.println("Description: " + descriptionAreaField.getText());
            System.out.println("Status: " + statutComboBox.getValue());
            System.out.println("Date: " + dateField.getValue());
            System.out.println("Reclamation: " + reclamationIdComboBox.getValue());

            // Get the reclamation ID directly from the ComboBox
            Integer reclamationId = reclamationIdComboBox.getValue();
            System.out.println("Reclamation ID: " + reclamationId);

            Reponse updatedReponse = new Reponse(
                    reponseToUpdate.getId(),
                    descriptionAreaField.getText(),
                    statutComboBox.getValue(),
                    dateField.getValue().atStartOfDay(),
                    reclamationId
            );

            // Update the response in the database
            reponseDAO.updateReponse(updatedReponse);
            System.out.println("✅ Réponse mise à jour avec succès !");

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La réponse a été mise à jour avec succès !");
            alert.showAndWait();

            // Close the popup
            closePopup();
        } catch (Exception e) {
            System.out.println("❌ ERROR: Erreur lors de la mise à jour !");
            e.printStackTrace();

            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la mise à jour");
            alert.setContentText("Une erreur s'est produite lors de la mise à jour de la réponse: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void closePopup() {
        Stage stage = (Stage) dateField.getScene().getWindow();
        stage.close();
    }


}
