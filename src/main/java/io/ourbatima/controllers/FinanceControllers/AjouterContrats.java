package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.financeModel.Contrat;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class AjouterContrats {

    @FXML
    private DatePicker Datefin;

    @FXML
    private DatePicker Datedebut;
    @FXML
    private ComboBox<String> contractType;
    private int projetid;
    @FXML
    private TextField montantTotaleField;

    @FXML
    private ComboBox<String> projectNames;
    @FXML
    private Button uploadButton;

    @FXML
    private ImageView imageView;
    private String imagePath;

   private ContratServise cs=new ContratServise();
    public void handleClose(ActionEvent event) {


            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close(); // Close the popup
        }

    @FXML
    public void initialize() {
        contractType.setItems(FXCollections.observableArrayList("Contrat Client", "Contrat Constructeur"));
        montantTotaleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d*)?")) { // Allow only numbers and decimal points
                montantTotaleField.setText(oldValue); // Revert to previous valid input
            }
        });

        uploadButton.setOnAction(event -> {
            try {
                uploadImage();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

    @FXML
    public void onContractTypeSelected() {
        String selectedType = contractType.getValue();



        if (selectedType != null) {
            List<Projet> projects = getProjectsFromDatabase();

// Extract only ID and Name, formatted as "ID - Name"
            List<String> projectInfoList = projects.stream()
                    .map(projet -> projet.getId_projet() + " - " + projet.getNomProjet())
                    .collect(Collectors.toList());

// Set extracted values into projectNames
            projectNames.setItems(FXCollections.observableArrayList(projectInfoList));
            projectNames.setVisible(!projects.isEmpty());

        }
    }



    public List<Projet> getProjectsFromDatabase() {
        List<Projet> projects = new ArrayList<>();

        try (Connection conn = getConnection()) {

            String query = "SELECT Id_projet , nomProjet   FROM projet ";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Id_projet");
                String name = rs.getString("nomProjet");
                Projet p = new Projet(id, name);
                System.out.println(p.getId_projet());
                System.out.println(p);
                projects.add(p);

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return projects;


    }


    public void projetselected(ActionEvent event) {
        String selectedValue = projectNames.getValue();
        String projectIdString = selectedValue.split(" - ")[0]; // Extracts "1"
         projetid = Integer.parseInt(projectIdString); // Converts "1" to integer
        System.out.println(projetid);





    }


    private void uploadImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
                // Ensure the directory exists
                File directory = new File("images");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Save the image to the folder
                File destinationFile = new File("images/" + selectedFile.getName());
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Display the image in ImageView
                imageView.setImage(new Image(destinationFile.toURI().toString()));

                // Store the path in the database
                 imagePath = "images/" + selectedFile.getName();



        }
    }


    public void ajouterContrat(ActionEvent event) {
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date localDate = Date.valueOf(Datefin.getValue()); // Get value from DatePicker
        java.sql.Date dateeee= Date.valueOf(Datedebut.getValue());


        Contrat con=new Contrat(contractType.getValue().toString(),new java.sql.Date(utilDate.getTime()),dateeee,imagePath,localDate,Double.parseDouble(montantTotaleField.getText()),projetid);

cs.insertContrat(con);
        Stage stage = (Stage) contractType.getScene().getWindow();
        stage.close();
    }




    }


