
package io.OurBatima.controllers.Reclamation;


import javafx.fxml.FXML;



import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
        import javafx.stage.Stage;

import java.sql.*;
        import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateReclamationController extends ActionView {

    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField statutTextField;
    @FXML
    private ComboBox<Integer> utilisateurIdComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final ReclamationDAO reclamationDAO = new ReclamationDAO();
    private Reclamation reclamationToUpdate;

    @FXML
    public void initialize() {
         // Load status options when the window opens
        System.out.println("UpdateReclamationController Initialized");
        updateButton.setOnAction(event -> updateReclamation());
    }

    /*private void loadStatuts() {
        List<String> statuts = Arrays.asList("Ouverte", "En cours", "Résolue", "Fermée");
        statutComboBox.setItems(FXCollections.observableArrayList(statuts));
    }*/

   public void setReclamationToUpdate(Reclamation reclamation) {
        this.reclamationToUpdate = reclamation;


        if (reclamation != null) {
            descriptionField.setText(reclamation.getDescription());
             statutTextField.setText(reclamation.getStatut());;
            datePicker.setValue(reclamation.getDate().toLocalDate());
            utilisateurIdComboBox.setValue(reclamation.getUtilisateurId());
        }
    }

    @FXML
    public void updateReclamation() {
        if ( descriptionField.getText().isEmpty() ||
                statutTextField.getText().isEmpty()|| datePicker.getValue() == null|| utilisateurIdComboBox.getValue() == null) {
            System.out.println("❌ ERROR: Remplissez tous les champs !");
            return;
        }

        try {
            Reclamation updatedReclamation = new Reclamation(

                    descriptionField.getText(),
                    statutTextField.getText(),
                    datePicker.getValue().atStartOfDay(),
                    utilisateurIdComboBox.getValue()


            );

            reclamationDAO.updateReclamation(updatedReclamation);
            System.out.println("✅ Réclamation mise à jour avec succès !");
            closePopup();
        } catch (Exception e) {
            System.out.println("❌ ERROR: Erreur lors de la mise à jour !");
            e.printStackTrace();
        }
    }

    @FXML
    public void closePopup() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setReclamationId(int idReclamation) {
        Reclamation reclamation = reclamationDAO.getReclamationById(idReclamation);
        setReclamationToUpdate(reclamation);
    }

    //public void setId(int id){}
}




