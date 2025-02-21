package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.financeModel.Contrat;
import io.ourbatima.core.model.financeModel.ContratDTO;
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
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class UpdateContratController {
private ContratServise cs=new ContratServise();
    @FXML
    private ComboBox<String> typeContratField;

    @FXML
    private DatePicker dateDebutField;
    @FXML
    private DatePicker dateFinField;
    @FXML
    private TextField montantTotalField;

    @FXML
    private ComboBox<String> projectNames;

    @FXML
    private ImageView imageView;
    @FXML
    private Button uploadButton;
    private String imagePath;
private int projetid ;
    private ContratDTO contratData;
    private Date datedd ;

    // Method to set the ContratDTO data and populate the fields
    public void setContratData(ContratDTO contratData) {
        this.contratData = contratData;
datedd=contratData.getDateSignature();
        // Populate the fields with the data

        typeContratField.setValue(contratData.getTypeContrat());
        LocalDate localDateFin = contratData.getDateFin()
                .toLocalDate();
        LocalDate localDateDebut = contratData.getDateDebut()
                .toLocalDate();


// Set the values in the DatePicker fields
        dateFinField.setValue(localDateFin);
        dateDebutField.setValue(localDateDebut);
        montantTotalField.setText(String.valueOf(contratData.getMontantTotal()));
        projectNames.setValue(contratData.getIdProjet() + "-" + contratData.getNomProjet());
         imagePath = contratData.isSignatureElectronique();
        System.out.println(imagePath);

        System.out.println("Image Path: " + imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {


            File imageFile = new File(imagePath);
            System.out.println("Checking image at: " + imageFile.getAbsolutePath());

            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);


            try {

            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No image path provided.");
        }

    }}

    public void Save(ActionEvent event) {
        System.out.println("this is project id "+projetid);
        Contrat con=new Contrat(contratData.getIdContrat(),typeContratField.getValue(),datedd,Date.valueOf(dateDebutField.getValue()),imagePath,Date.valueOf(dateFinField.getValue()),Double.parseDouble(montantTotalField.getText()),projetid);
        cs.update(con);

        Stage stage = (Stage) typeContratField.getScene().getWindow();
        stage.close();


    }

    @FXML
    public void initialize() {
        typeContratField.setItems(FXCollections.observableArrayList("Contrat Client", "Contrat Constructeur"));
        montantTotalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d*)?")) { // Allow only numbers and decimal points
                montantTotalField.setText(oldValue); // Revert to previous valid input
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
            File destinationFile = new File("images" + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Display the image in ImageView
            imageView.setImage(new Image(destinationFile.toURI().toString()));

            // Store the path in the database
            imagePath = "images/" + selectedFile.getName();
            java.util.Date utilDate = new java.util.Date();

            datedd=new java.sql.Date(utilDate.getTime());


        }
    }

    @FXML
    public void onContractTypeSelected() {
        String selectedType = typeContratField.getValue();


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


    public void annuler(ActionEvent event) {
        Stage stage = (Stage) typeContratField.getScene().getWindow();
        stage.close();
    }
    }

