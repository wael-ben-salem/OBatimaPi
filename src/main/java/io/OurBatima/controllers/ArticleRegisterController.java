package io.OurBatima.controllers;

import io.OurBatima.core.Dao.Stock.ArticleDAO;
import io.OurBatima.core.model.Article;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ArticleRegisterController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField photoField;
    @FXML
    private TextField stockIdField;
    @FXML
    private TextField fournisseurIdField;
    @FXML
    private TextField etapeProjetIdField;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink cancelLink;

    private ArticleDAO articleDAO;

    public ArticleRegisterController() {
        articleDAO = new ArticleDAO(); // Initialize the DAO
    }

    @FXML
    private void handleRegister() {
        // Retrieve input values
        String nom = nomField.getText();
        String description = descriptionField.getText();
        String prix = prixField.getText();
        String photo = photoField.getText();
        int stockId = Integer.parseInt(stockIdField.getText());
        int fournisseurId = Integer.parseInt(fournisseurIdField.getText());
        int etapeProjetId = Integer.parseInt(etapeProjetIdField.getText());

        // Create a new Article object
        Article article = new Article(0, nom, description, prix, photo, stockId, fournisseurId, etapeProjetId);

        // Save the article using the DAO
        boolean isSaved = articleDAO.saveArticle(article);

        // Show feedback to the user
        if (isSaved) {
            showAlert("Succès", "L'article a été enregistré avec succès.", AlertType.INFORMATION);
            clearFields(); // Clear the input fields after successful registration
        } else {
            showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement de l'article.", AlertType.ERROR);
        }
    }

    @FXML
    private void goToHome() {
        // Logic to navigate back to the home page
        // This could be a scene switch or similar
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        prixField.clear();
        photoField.clear();
        stockIdField.clear();
        fournisseurIdField.clear();
        etapeProjetIdField.clear();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}