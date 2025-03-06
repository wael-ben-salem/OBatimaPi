package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.FinanceService.abonnementService;
import io.ourbatima.core.model.financeModel.abonnement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class cardAbonnement implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label Prix;
    @FXML private Label Duree;
    @FXML private HBox root;

  private  ListAbonnement ListAbonnement;
    private abonnement abo;
    private Context context;
private abonnementService as=new abonnementService();

    public void setUserData(abonnement abo) {
        this.abo = abo;
        updateUI();
        
    }
    @FXML
   public void onInit(Context context){
        this.context = context;
        updateUI();


    }

    public HBox getView() {
        if (root == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/cardAbonnement.fxml"));
                loader.setController(this);
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    private void updateUI() {
        nameLabel.setText(abo.getNomAbonnement() );
        Duree.setText(abo.getDuree());
        Prix.setText(String.valueOf(abo.getPrix()));


    }
    public void setListAbonnemant(ListAbonnement ListAbonnement) {
        this.ListAbonnement = ListAbonnement;
    }


    public void updateUser(ActionEvent event) {
        try {
            // Load the FXML file for the update popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Finance_vews/UpdateAbonnement .fxml"));
            Parent root = loader.load();

            // Get the controller for the popup
            UpdateAbonnement updateController = loader.getController();

            // Pass the selected ContratDTO object to the popup controller
            updateController.setContratData(abo);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            popupStage.setTitle("Update Abonnemant");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); // Wait for the popup to close

            // Refresh the table after the popup is closed
          ListAbonnement.loadUsers();
        } catch (IOException e) {
            System.err.println("Error opening UpdateContrat.fxml: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void deleteUser(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer Confirmation");
        alert.setContentText("vouler vous supprimer cette abonnement?");

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // If the user clicks "OK" (or "Yes"), delete the data
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Call the service to delete the contract
                as.DeliteAbonnement(abo.getIdAbonnement()); // Assuming `deleteContrat` is a method in your service

                if (ListAbonnement != null) {
                    ListAbonnement.loadUsers();
                }
                // Refresh the table after deletion


            } catch (Exception e) {
                System.err.println("Error deleting contract: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
