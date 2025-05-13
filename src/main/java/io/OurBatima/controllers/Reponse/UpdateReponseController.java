package io.OurBatima.controllers.Reponse;

import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import io.OurBatima.core.model.Reponse;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    }

    private void loadReclamations() {
        // Clear existing items
        reclamationIdComboBox.getItems().clear();

        // Fetch all reclamations from the database and populate the ComboBox
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
        reclamationIdComboBox.setItems(FXCollections.observableArrayList(reclamations));

        // Set up a custom cell factory to display reclamation details
        reclamationIdComboBox.setConverter(new StringConverter<Reclamation>() {
            @Override
            public String toString(Reclamation reclamation) {
                if (reclamation == null) {
                    return "";
                }
                return "ID: " + reclamation.getId() + " - " +
                       truncateText(reclamation.getDescription(), 30) + " - " +
                       reclamation.getStatut();
            }

            @Override
            public Reclamation fromString(String string) {
                return null; // Not needed for this use case
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
        if (descriptionAreaField.getText().isEmpty() ||
                statutComboBox.getValue() == null || dateField.getValue() == null || reclamationIdComboBox.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        try {
            // Make sure we have a valid reclamation selected
            if (reclamationIdComboBox.getValue() == null) {
                System.out.println("❌ ERROR: Veuillez sélectionner une réclamation!");
                return;
            }

            Reponse updatedReponse = new Reponse(
                    reponseToUpdate.getId(),
                    descriptionAreaField.getText(),
                    statutComboBox.getValue(),
                    dateField.getValue().atStartOfDay(),
                    reclamationIdComboBox.getValue().getId()
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
