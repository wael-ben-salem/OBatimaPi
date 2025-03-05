package io.ourbatima.controllers.projet;

import io.ourbatima.controllers.terrain.AjoutTerrain;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Terrain.TerrainDAO;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.services.GeminiAPI;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import io.ourbatima.core.model.Utilisateur.Client;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Terrain;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Random;
import java.util.regex.Pattern;

public class AjoutProjet extends ActionView {

    @FXML
    private TextField clientTextField;
    @FXML
    private TextField equipeTextField;
    @FXML
    private TextField terrainTextField;
    @FXML
    private TextField nomProjetTextField;
    @FXML
    private TextField typeTextField;
    @FXML
    private TextField styleArchTextField;
    @FXML
    private TextField budgetTextField;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button openAjoutTerrainButton;
    @FXML


    private UtilisateurDAO clientDAO = new UtilisateurDAO();
    private EquipeDAO equipeDAO = new EquipeDAO();
    private TerrainDAO terrainDAO = new TerrainDAO();
    private ProjetDAO projetDAO = new ProjetDAO();
    private String superficie;
    private String styleArch;
    private String emplacement;
    private String type;

    @FXML
    private void initialize() {
        System.out.println("AjoutProjet Controller Initialized");
        ajouterButton.setOnAction(event -> handleAddProjet());
        addInputListeners();
    }


    private void loadLastData() {
        Terrain lastTerrain = terrainDAO.getLastInsertedTerrain();
        if (lastTerrain != null) {
            this.emplacement = lastTerrain.getEmplacement(); // Store emplacement
            this.superficie = lastTerrain.getSuperficie() != null ? lastTerrain.getSuperficie().toString() : null; // Store superficie
        }

        Projet lastProjet = projetDAO.getLastInsertedProjet();
        if (lastProjet != null) {
            this.type = lastProjet.getType();
            this.styleArch = lastProjet.getStyleArch();
        }
    }

    private void handleGeminiAPIEstimation() {
        loadLastData();

        GeminiAPI geminiAPI = new GeminiAPI();
        String estimation = geminiAPI.getEstimation(
                this.styleArch,
                this.superficie,
                this.emplacement,
                this.type
        );

        System.out.println("Sending to GeminiAPI:");
        System.out.println("Style Architecture: " + this.styleArch);
        System.out.println("Superficie: " + this.superficie);
        System.out.println("Emplacement: " + this.emplacement);
        System.out.println("Type: " + this.type);

        System.out.println("Estimation: " + estimation);
        showEstimationWindow(estimation);
    }



    @FXML
    private void openAjoutTerrain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Terrain/ajoutTerrain.fxml"));
            Parent root = loader.load();

            AjoutTerrain ajoutTerrainController = loader.getController();
            ajoutTerrainController.setAjoutProjetController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Terrain");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (ajoutTerrainController.getSuperficie() != null) {
                this.superficie = ajoutTerrainController.getSuperficie();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTerrainTextField(String emplacement) {
        System.out.println("Setting terrainTextField: " + emplacement);
        terrainTextField.setText(emplacement);
    }

    private void addInputListeners() {
        nomProjetTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        clientTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        equipeTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        terrainTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        typeTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        styleArchTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
        budgetTextField.textProperty().addListener((observable, oldValue, newValue) -> validateInput());
    }

    private void validateInput() {
        boolean isValid = isValidNom(nomProjetTextField.getText()) &&
                isValidEmail(clientTextField.getText().trim()) &&
                !equipeTextField.getText().trim().isEmpty() &&
                !terrainTextField.getText().trim().isEmpty() &&
                !typeTextField.getText().trim().isEmpty() &&
                !styleArchTextField.getText().trim().isEmpty();

        try {
            new BigDecimal(budgetTextField.getText());
        } catch (NumberFormatException e) {
            isValid = false;
        }

        ajouterButton.setDisable(!isValid);
    }

    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }

    @FXML
    private void handleAddProjet() {
        String nomProjet = nomProjetTextField.getText().trim();
        String clientEmail = clientTextField.getText().trim();
        String nomEquipe = equipeTextField.getText().trim();
        String emplacement = terrainTextField.getText().trim();
        String type = typeTextField.getText();
        String styleArch = styleArchTextField.getText().trim();

        BigDecimal budget;
        try {
            budget = new BigDecimal(budgetTextField.getText());
        } catch (NumberFormatException e) {
            showError("Format de budget invalide");
            return;
        }

        if (nomProjet.isEmpty() || !isValidNom(nomProjet)) {
            showError("Nom du projet invalide");
            return;
        }

        if (clientEmail.isEmpty() || !isValidEmail(clientEmail)) {
            showError("Email du client invalide");
            return;
        }

        if (nomEquipe.isEmpty()) {
            showError("Nom de l'équipe est requis");
            return;
        }

        if (emplacement.isEmpty()) {
            showError("Emplacement du terrain est requis");
            return;
        }

        if (type.isEmpty()) {
            showError("Type de projet est requis");
            return;
        }

        if (styleArch.isEmpty()) {
            showError("Style d'architecture est requis");
            return;
        }

        int clientId = getClientIdByEmail(clientEmail);
        int equipeId = getEquipeIdByName(nomEquipe);
        int Id_terrain = getTerrainByEmplacement(emplacement);

        // Check if IDs are valid
        if (clientId == -1 || equipeId == -1 || Id_terrain == -1) {
            showError("Détails du client, de l'équipe ou du terrain invalides");
            return;
        }

        Projet projet = new Projet(nomProjet, equipeId, clientId, Id_terrain, budget, type, styleArch, new Timestamp(System.currentTimeMillis()));

        int projetId = projetDAO.addProjet(projet);

        if (projetId != -1) {
            showSuccess("Projet ajouté avec succès");
            resetFields();
        } else {
            showError("Erreur lors de l'ajout du projet");
        }
    }

    private void showEstimationWindow(String estimation) {
        try {
            System.out.println("Loading EstimationWindow.fxml...");

            // Load the FXML file for the estimation window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Projet/estimationWindow.fxml"));
            Parent root = loader.load();

            System.out.println("FXML loaded successfully.");

            // Get the controller and pass the estimation data
            EstimationWindowController controller = loader.getController();
            controller.setEstimation(estimation);

            System.out.println("Estimation set in controller: " + estimation);

            Stage stage = new Stage();
            stage.setTitle("Estimation du Projet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            System.out.println("Estimation window shown.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading EstimationWindow.fxml: " + e.getMessage());
        } finally {
            isEstimationWindowOpen = false;
        }
    }

    private int getClientIdByEmail(String email) {
        return clientDAO.getClientByEmail(email);
    }

    private int getEquipeIdByName(String equipeName) {
        return equipeDAO.getEquipeByName(equipeName);
    }

    private int getTerrainByEmplacement(String emplacement) {
        Terrain terrain = terrainDAO.getTerrainByEmplacement(emplacement);
        return (terrain != null) ? terrain.getId_terrain() : -1;
    }

    private boolean isValidNom(String nom) {
        return nom.length() >= 3 && nom.length() <= 100;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isEstimationWindowOpen = false;

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        ButtonType viewEstimationButton = new ButtonType("Estimation du projet");
        alert.getButtonTypes().setAll(ButtonType.OK, viewEstimationButton);

        // Handle button clicks
        alert.showAndWait().ifPresent(response -> {
            if (response == viewEstimationButton && !isEstimationWindowOpen) {
                isEstimationWindowOpen = true;
                handleGeminiAPIEstimation();
            }
        });
    }

    private void resetFields() {
        nomProjetTextField.clear();
        clientTextField.clear();
        equipeTextField.clear();
        terrainTextField.clear();
        typeTextField.clear();
        styleArchTextField.clear();
        budgetTextField.clear();
    }
}
