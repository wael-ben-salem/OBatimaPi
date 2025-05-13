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
    private ComboBox<Reclamation> reclamationIdComboBox;
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
     * Adds manual reclamation items to the ComboBox if database loading fails
     */
    private void addManualReclamations() {
        System.out.println("Adding manual reclamation items...");

        // Create some dummy reclamations
        List<Reclamation> manualReclamations = new ArrayList<>();
        manualReclamations.add(new Reclamation(1, "Reclamation 1", "NEW", java.time.LocalDateTime.now(), 1));
        manualReclamations.add(new Reclamation(2, "Reclamation 2", "In Progress", java.time.LocalDateTime.now(), 1));
        manualReclamations.add(new Reclamation(3, "Reclamation 3", "Resolved", java.time.LocalDateTime.now(), 1));

        // Add them to the ComboBox
        reclamationIdComboBox.setItems(FXCollections.observableArrayList(manualReclamations));

        System.out.println("Added " + manualReclamations.size() + " manual reclamation items");
    }

    private void loadReclamations() {
        try {
            System.out.println("Starting to load reclamations...");

            // Clear existing items
            reclamationIdComboBox.getItems().clear();

            // Fetch all reclamations from the database and populate the ComboBox
            List<Reclamation> reclamations = reclamationDAO.getAllReclamations();

            // Check if we have any reclamations
            if (reclamations == null) {
                System.out.println("ERROR: reclamations list is null");
                return;
            }

            if (reclamations.isEmpty()) {
                System.out.println("No reclamations found in the database");

                // Add a dummy reclamation for testing
                Reclamation dummyReclamation = new Reclamation(1, "Test Reclamation", "NEW",
                        java.time.LocalDateTime.now(), 1);
                reclamations.add(dummyReclamation);
                System.out.println("Added a dummy reclamation for testing");
            }

            // Create an observable list
            javafx.collections.ObservableList<Reclamation> reclamationList =
                FXCollections.observableArrayList(reclamations);

            // Add reclamations to the ComboBox
            reclamationIdComboBox.setItems(reclamationList);

            // Debug output
            System.out.println("Loaded " + reclamations.size() + " reclamations");
            for (Reclamation r : reclamations) {
                System.out.println("Reclamation ID: " + r.getId() + ", Description: " + r.getDescription());
            }
        } catch (Exception e) {
            System.out.println("ERROR loading reclamations: " + e.getMessage());
            e.printStackTrace();
        }

        // Alternative approach: Use a cell factory to customize how items appear in the dropdown
        reclamationIdComboBox.setCellFactory(param -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    setText("Reclamation #" + reclamation.getId());
                }
            }
        });

        // Also set a button cell to display the selected item in the ComboBox button
        reclamationIdComboBox.setButtonCell(new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    setText("Reclamation #" + reclamation.getId());
                }
            }
        });
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
            // Find the reclamation by ID and set it in the ComboBox
            for (Reclamation reclamation : reclamationIdComboBox.getItems()) {
                if (reclamation.getId() == reponse.getIdReclamation()) {
                    reclamationIdComboBox.setValue(reclamation);
                    break;
                }
            }
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

            Integer reclamationId;
            try {
                reclamationId = reclamationIdComboBox.getValue().getId();
                System.out.println("Reclamation ID: " + reclamationId);
            } catch (Exception e) {
                System.out.println("Error getting reclamation ID: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            Reponse updatedReponse = new Reponse(
                    reponseToUpdate.getId(),
                    descriptionAreaField.getText(),
                    statutComboBox.getValue(),
                    dateField.getValue().atStartOfDay(),
                    reclamationId
            );

            reponseDAO.updateReponse(updatedReponse);
            System.out.println("✅ Réponse mise à jour avec succès !");
            closePopup();
        } catch (Exception e) {
            System.out.println("❌ ERROR: Erreur lors de la mise à jour !");
            e.printStackTrace();
        }
    }

    @FXML
    public void closePopup() {
        Stage stage = (Stage) dateField.getScene().getWindow();
        stage.close();
    }


}
