package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class CreateAbonnement {

    @FXML
    private TextField nomField;

    @FXML
    private TextField PrixField;

    @FXML
    private TextField DureeField;
    private abonnementService as=new abonnementService();

    @FXML
    public void initialize(){
        PrixField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d*)?")) { // Allow only numbers and decimal points
                PrixField.setText(oldValue); // Revert to previous valid input
            }
        });

        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) { // Allow only letters and spaces
                nomField.setText(oldValue); // Revert to previous valid input
            }
        });}

    public void close(ActionEvent event) {
        ((Stage) nomField.getScene().getWindow()).close();

    }

    public void Ajouter(ActionEvent event) {
        if (Objects.equals(nomField.getText(), "")|| Objects.equals(DureeField.getText(), "")||(Objects.equals(PrixField.getText(), ""))){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
        }
        else {

            abonnement abo = new abonnement(nomField.getText(), DureeField.getText(), Double.parseDouble(PrixField.getText()));


            ((Stage) nomField.getScene().getWindow()).close();
            as.insertAbonnemant(abo);


        }
    }
}
