package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class UpdateAbonnement {
    @FXML
    private TextField nomField;

    @FXML
    private TextField PrixField;

    @FXML
    private TextField DureeField;


private abonnementService  as=new abonnementService();
    abonnement Abonnement;
    public void setContratData(abonnement abo) {
        this.Abonnement=abo;

        nomField.setText(abo.getNomAbonnement());
        DureeField.setText(abo.getDuree());
        PrixField.setText(String.valueOf(abo.getPrix()));

    }
    @FXML

    public void close(ActionEvent event) {
        ((Stage) nomField.getScene().getWindow()).close();


    }
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
    });







    }

    public void updateUser(ActionEvent event) {
        if (Objects.equals(nomField.getText(), "")|| Objects.equals(DureeField.getText(), "")||(Objects.equals(PrixField.getText(), ""))){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled!");
            alert.showAndWait();
        }
        else{
        abonnement abon=new abonnement(Abonnement.getIdAbonnement(), nomField.getText(), DureeField.getText(), Double.parseDouble(PrixField.getText()));

        as.upadteAbonnement(abon);
        ((Stage) nomField.getScene().getWindow()).close();


    }}
}
