package io.ourbatima.controllers;

import io.ourbatima.core.Dao.Stock.ArticleDAO;
import io.ourbatima.core.Dao.Stock.FournisseurDAO;
import io.ourbatima.core.Dao.Stock.StockDAO;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Article;
import io.ourbatima.core.model.Fournisseur;
import io.ourbatima.core.model.Stock;
import io.ourbatima.core.model.EtapeProjet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleController extends ActionView {

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField photoField;

    @FXML
    private ComboBox<String> stockComboBox;

    @FXML
    private ComboBox<String> fournisseurComboBox;

    @FXML
    private ComboBox<String> etapeProjetComboBox;

    private final ArticleDAO articleDAO = new ArticleDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();

    // Maps to store the relationship between names and IDs
    private final Map<String, Integer> stockNameToIdMap = new HashMap<>();
    private final Map<String, Integer> fournisseurNameToIdMap = new HashMap<>();
    private final Map<String, Integer> etapeNomToIdMap = new HashMap<>();

    public void loadStocks() {
        stockComboBox.getItems().clear();
        stockNameToIdMap.clear();
        List<Stock> stocks = stockDAO.getAllStocks();
        for (Stock stock : stocks) {
            stockComboBox.getItems().add(stock.getNom());
            stockNameToIdMap.put(stock.getNom(), stock.getId());
        }
    }

    public void loadFournisseurs() {
        fournisseurComboBox.getItems().clear();
        fournisseurNameToIdMap.clear();
        List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();
        for (Fournisseur fournisseur : fournisseurs) {
            fournisseurComboBox.getItems().add(fournisseur.getNom());
            fournisseurNameToIdMap.put(fournisseur.getNom(), fournisseur.getId());
        }
    }

    public void loadEtapesProjets() {
        etapeProjetComboBox.getItems().clear();
        etapeNomToIdMap.clear();
        List<EtapeProjet> etapeProjets = etapeProjetDAO.getAllEtapeProjets();
        for (EtapeProjet etape : etapeProjets) {
            etapeProjetComboBox.getItems().add(etape.getNomEtape());
            etapeNomToIdMap.put(etape.getNomEtape(), etape.getId_etapeProjet());
        }
    }

    public void goToHome(ActionEvent actionEvent) {
        context.routes().setView("drawer");
    }

    public void handleAddArticle(ActionEvent actionEvent) {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String prixUnitaire = prixField.getText().trim();
        String photo = photoField.getText().trim();

        String stockName = stockComboBox.getValue();
        String fournisseurName = fournisseurComboBox.getValue();
        String etapeProjetName = etapeProjetComboBox.getValue();

        // Validate inputs
        if (nom.length() < 3) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le nom doit contenir au moins 3 caractères.");
            return;
        }

        if (!prixUnitaire.isEmpty()) {
            try {
                Float.parseFloat(prixUnitaire); // Check if the price is a valid float
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Erreur de saisie", "Le prix doit être un nombre valide.");
                return;
            }
        }

        if (stockName == null) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le Stock est obligatoire.");
            return;
        }

        if (fournisseurName == null) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "Le Fournisseur est obligatoire.");
            return;
        }

        if (etapeProjetName == null) {
            showAlert(AlertType.ERROR, "Erreur de saisie", "L'Étape de projet est obligatoire.");
            return;
        }

        // Get the IDs from the maps
        Integer stockId = stockNameToIdMap.get(stockName);
        Integer fournisseurId = fournisseurNameToIdMap.get(fournisseurName);
        Integer etapeId = etapeNomToIdMap.get(etapeProjetName);

        // Create article object
        Article article = new Article();
        article.setNom(nom);
        article.setDescription(description);
        article.setPrixUnitaire(prixUnitaire);
        article.setPhoto(photo);
        article.setStockId(stockId);
        article.setFournisseurId(fournisseurId);
        article.setEtapeProjetId(etapeId);

        boolean isSaved = articleDAO.saveArticle(article);

        if (isSaved) {
            showAlert(AlertType.INFORMATION, "Succès", "L'article a été ajouté avec succès.");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de l'ajout de l'article.");
        }
    }

    public void handleChoosePhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            photoField.setText(selectedFile.getAbsolutePath());
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
        descriptionField.clear();
        prixField.clear();
        photoField.clear();
        stockComboBox.getSelectionModel().clearSelection();
        fournisseurComboBox.getSelectionModel().clearSelection();
        etapeProjetComboBox.getSelectionModel().clearSelection();
    }
}