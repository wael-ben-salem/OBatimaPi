package io.OurBatima.controllers.Reponse;

import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reponse;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.awt.*;

public class UpdateReponseController extends ActionView {

    @FXML
    private javafx.scene.control.TextArea descriptionAreaField;
    @FXML
    private javafx.scene.control.TextField statutTextField;
    @FXML
    private ComboBox<Integer> reclamationIdComboBox;
    @FXML
    private DatePicker dateField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final ReponseDAO reponseDAO = new ReponseDAO();
    private Reponse reponseToUpdate;

    @FXML
    public void initialize() {
        // Load status options when the window opens
        System.out.println("UpdateReclamationController Initialized");
       // saveButton.setOnAction(e -> updateReponse());
       // cancelButton.setOnAction(event -> closePopup());
    }

    public void setReponseToUpdate(Reponse reponse) {
        this.reponseToUpdate = reponse;

        if (reponse != null) {
            descriptionAreaField.setText(reponse.getDescription());
            statutTextField.setText(reponse.getStatut());
            dateField.setValue(reponse.getDate().toLocalDate());
            reclamationIdComboBox.setValue(reponse.getIdReclamation());
        }
    }

    @FXML
    public void updateReponse() {
        if (descriptionAreaField.getText().isEmpty() ||
                statutTextField.getText().isEmpty() || dateField.getValue() == null || reclamationIdComboBox.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        try {
            Reponse updatedReponse = new Reponse(
                    reponseToUpdate.getId(),
                    descriptionAreaField.getText(),
                    statutTextField.getText(),
                    dateField.getValue().atStartOfDay(),
                    reclamationIdComboBox.getValue()
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
