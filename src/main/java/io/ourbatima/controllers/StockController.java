package io.ourbatima.controllers;

import io.ourbatima.core.Dao.Stock.StockDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Stock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class StockController extends ActionView {

    @FXML
    private TextField nomField;

    @FXML
    private TextField emplacementField;

    @FXML
    private DatePicker dateCreationField;

    private final StockDAO stockDAO = new StockDAO();

    public void goToHome(ActionEvent actionEvent) {
        context.routes().setView("drawer");
    }

    public void handleAddStock(ActionEvent actionEvent) {
        // Retrieve data from the input fields
        String nom = nomField.getText().trim();
        String emplacement = emplacementField.getText().trim();
        String dateCreation = dateCreationField.getValue() != null ? dateCreationField.getValue().toString() : "";

        // Validate the input fields
        if (nom.length() < 3 || emplacement.length() < 3 || dateCreation.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le nom et l'emplacement doivent contenir au moins 3 caractères et la date de création doit être sélectionnée.");
            return;
        }

        // Check if the stock already exists
        if (stockDAO.isStockExist(nom)) {
            showAlert(AlertType.ERROR, "Erreur", "Un stock avec ce nom existe déjà.");
            return;
        }

        // Create a new Stock object
        Stock stock = new Stock();
        stock.setNom(nom);
        stock.setEmplacement(emplacement);
        stock.setDateCreation(dateCreation);

        // Save the stock to the database
        boolean isSaved = stockDAO.saveStock(stock);

        // Provide feedback to the user
        if (isSaved) {
            showAlert(AlertType.INFORMATION, "Succès", "Le stock a été ajouté avec succès.");
            clearFields(); // Clear the input fields after successful addition
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout du stock.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomField.clear();
        emplacementField.clear();
        dateCreationField.setValue(null); // Clear the DatePicker
    }
}