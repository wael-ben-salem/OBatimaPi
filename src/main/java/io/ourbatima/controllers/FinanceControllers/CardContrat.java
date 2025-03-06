package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.financeModel.Contrat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CardContrat implements Initializable {
    public Label Projet;
    public Label Date_debut;
    public Label Date_fin;
    public Label Montant;
    private Contrat con;
    private Context context;

    private ContratServise cs=new ContratServise();
    private ContatsClient contatsClient ;
    public void setUserData(Contrat user) {


            this.con = user;
            updateUI();


    }
    @FXML
    public void onInit(Context context){
        this.context = context;
        updateUI();


    }

    private void updateUI() {
       Projet pjt= cs.getProjetbyid(con.getIdProjet());
        System.out.println(pjt.getNomProjet()+"name taaa l contrat");
        String projetsane=pjt.getNomProjet();
        Projet.setText(projetsane);
        Date_debut.setText(con.getDateDebut().toString());
        Date_fin.setText(String.valueOf(con.getDateFin()));
        Montant.setText(String.valueOf(con.getMontantTotal()));






    }

    public void setListAbonnemant(ContatsClient contatsClient) {

        this.contatsClient=contatsClient;
    }






    public void openConsulter(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/Finance_vews/ConsulterContratAjouter.fxml"));
        Parent root = loader.load();

        // Get the controller instance created by the FXMLLoader
        ConsulterContratAjouter controller = loader.getController();

        // Set the Contrat object and load data
        controller.setdataCOntrat(con);

        // Show the new stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
