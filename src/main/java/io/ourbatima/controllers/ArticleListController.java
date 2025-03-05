package io.ourbatima.controllers;

import io.ourbatima.core.model.Article;
import io.ourbatima.core.model.Fournisseur;
import io.ourbatima.core.model.Stock;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.Dao.Stock.ArticleDAO;
import io.ourbatima.core.Dao.Stock.StockDAO;
import io.ourbatima.core.Dao.Stock.FournisseurDAO;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.interfaces.ActionView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.List;

public class ArticleListController extends ActionView {

    @FXML private GridPane articleGrid;
    private final ArticleDAO articleDAO = new ArticleDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();

    @FXML
    private void initialize() {
        loadArticles();
    }

    @FXML
    private void loadArticles() {
        List<Article> articles = articleDAO.getAllArticles();
        List<Stock> allStocks = stockDAO.getAllStocks();
        List<Fournisseur> allFournisseurs = fournisseurDAO.getAllFournisseurs();
        List<EtapeProjet> allEtapes = etapeProjetDAO.getAllEtapeProjets();

        // Clear existing rows (keep header row)
        articleGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        int rowIndex = 1;
        for (Article article : articles) {
            // Create UI components
            TextField idField = createReadOnlyField(String.valueOf(article.getId()));
            TextField nomField = createEditableField(article.getNom());
            TextField descriptionField = createEditableField(article.getDescription());
            TextField prixUnitaireField = createEditableField(article.getPrixUnitaire());
            ImageView photoView = createImageView(article.getPhoto());

            // Stock ComboBox
            ComboBox<Stock> stockCombo = new ComboBox<>();
            stockCombo.getItems().addAll(allStocks);
            stockCombo.setConverter(createStockConverter());
            stockCombo.setValue(findStockById(allStocks, article.getStockId()));

            // Fournisseur ComboBox
            ComboBox<Fournisseur> fournisseurCombo = new ComboBox<>();
            fournisseurCombo.getItems().addAll(allFournisseurs);
            fournisseurCombo.setConverter(createFournisseurConverter());
            fournisseurCombo.setValue(findFournisseurById(allFournisseurs, article.getFournisseurId()));

            // Etape Projet ComboBox
            ComboBox<EtapeProjet> etapeProjetCombo = new ComboBox<>();
            etapeProjetCombo.getItems().addAll(allEtapes);
            etapeProjetCombo.setConverter(createEtapeProjetConverter());
            etapeProjetCombo.setValue(findEtapeProjetById(allEtapes, article.getEtapeProjetId()));

            // Action buttons
            Button saveButton = createSaveButton(article, nomField, descriptionField,
                    prixUnitaireField, stockCombo, fournisseurCombo, etapeProjetCombo);

            Button deleteButton = createDeleteButton(article);

            // Add to grid
            articleGrid.addRow(rowIndex++,
                    idField, nomField, descriptionField, prixUnitaireField,
                    photoView, stockCombo, fournisseurCombo, etapeProjetCombo,
                    saveButton, deleteButton
            );
        }
    }

    // Helper methods
    private TextField createEditableField(String text) {
        TextField field = new TextField(text);
        field.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return field;
    }

    private TextField createReadOnlyField(String text) {
        TextField field = new TextField(text);
        field.setEditable(false);
        field.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return field;
    }

    private ImageView createImageView(String imageUrl) {
        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        return imageView;
    }

    private StringConverter<Stock> createStockConverter() {
        return new StringConverter<Stock>() {
            @Override
            public String toString(Stock stock) {
                return stock != null ? stock.getNom() : "";
            }

            @Override
            public Stock fromString(String string) {
                return null; // Not needed for display
            }
        };
    }

    private StringConverter<Fournisseur> createFournisseurConverter() {
        return new StringConverter<Fournisseur>() {
            @Override
            public String toString(Fournisseur fournisseur) {
                return fournisseur != null ? fournisseur.getNom() + " " + fournisseur.getPrenom() : "";
            }

            @Override
            public Fournisseur fromString(String string) {
                return null; // Not needed for display
            }
        };
    }

    private StringConverter<EtapeProjet> createEtapeProjetConverter() {
        return new StringConverter<EtapeProjet>() {
            @Override
            public String toString(EtapeProjet etapeProjet) {
                return etapeProjet != null ? etapeProjet.getNomEtape() : "";
            }

            @Override
            public EtapeProjet fromString(String string) {
                return null; // Not needed for display
            }
        };
    }

    private Stock findStockById(List<Stock> stocks, int id) {
        return stocks.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private Fournisseur findFournisseurById(List<Fournisseur> fournisseurs, int id) {
        return fournisseurs.stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private EtapeProjet findEtapeProjetById(List<EtapeProjet> etapes, int id) {
        return etapes.stream()
                .filter(e -> e.getId_etapeProjet() == id)
                .findFirst()
                .orElse(null);
    }

    private Button createSaveButton(Article article, TextField nomField, TextField descriptionField,
                                    TextField prixUnitaireField, ComboBox<Stock> stockCombo,
                                    ComboBox<Fournisseur> fournisseurCombo, ComboBox<EtapeProjet> etapeProjetCombo) {
        Button button = new Button("Save");
        button.setOnAction(e -> handleSave(
                article,
                nomField.getText(),
                descriptionField.getText(),
                prixUnitaireField.getText(),
                stockCombo.getValue(),
                fournisseurCombo.getValue(),
                etapeProjetCombo.getValue()
        ));
        return button;
    }

    private Button createDeleteButton(Article article) {
        Button button = new Button("Delete");
        button.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        button.setOnAction(e -> handleDelete(article));
        return button;
    }

    // Event handlers
    private void handleSave(Article article, String nom, String description, String prixUnitaire,
                            Stock selectedStock, Fournisseur selectedFournisseur, EtapeProjet selectedEtapeProjet) {
        try {
            article.setNom(nom);
            article.setDescription(description);
            article.setPrixUnitaire(prixUnitaire);

            if (selectedStock != null) {
                article.setStockId(selectedStock.getId());
            }

            if (selectedFournisseur != null) {
                article.setFournisseurId(selectedFournisseur.getId());
            }

            if (selectedEtapeProjet != null) {
                article.setEtapeProjetId(selectedEtapeProjet.getId_etapeProjet());
            }

            if (articleDAO.updateArticle(article)) {
                System.out.println("Successfully updated article: " + article.getId());
            } else {
                System.out.println("Failed to update article: " + article.getId());
            }
        } catch (NumberFormatException ex) {
            System.err.println("Invalid number format: " + ex.getMessage());
        }
    }

    private void handleDelete(Article article) {
        if (articleDAO.deleteArticle(article.getId())) {
            System.out.println("Successfully deleted article: " + article.getId());
            loadArticles(); // Refresh the list
        } else {
            System.out.println("Failed to delete article: " + article.getId());
        }
    }
}