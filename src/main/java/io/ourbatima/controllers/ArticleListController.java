package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.model.Article;
import io.ourbatima.core.model.Fournisseur;
import io.ourbatima.core.model.Stock;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.Dao.Stock.ArticleDAO;
import io.ourbatima.core.Dao.Stock.StockDAO;
import io.ourbatima.core.Dao.Stock.FournisseurDAO;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.interfaces.ActionView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ArticleListController extends ActionView {

    @FXML private GridPane articleGrid;
    private final ArticleDAO articleDAO = new ArticleDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();

    private static final String LOG_FILE_NAME = "log.txt";
    private static final String DEFAULT_PHOTO_PATH = "C:\\xest.png";

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        loadArticles(); // Automatically load articles on initialization
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
            TextField nomField = createEditableField(article.getNom());
            TextField descriptionField = createEditableField(article.getDescription());
            TextField prixUnitaireField = createEditableField(article.getPrixUnitaire());
            ImageView photoView = createImageView(article.getPhoto());
            photoView.setOnMouseClicked(e -> handlePhotoChange(photoView, article));

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
            Button saveButton = createIconButton("pencil.png", "Save");
            saveButton.setOnAction(e -> handleSave(article, nomField, descriptionField, prixUnitaireField, stockCombo, fournisseurCombo, etapeProjetCombo, photoView));

            Button deleteButton = createIconButton("bin.png", "Delete");
            deleteButton.setOnAction(e -> handleDelete(article));

            // Add to grid
            articleGrid.addRow(rowIndex++,
                    nomField, descriptionField, prixUnitaireField,
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

    private ImageView createImageView(String imageUrl) {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(imageUrl);
            imageView.setImage(image);
        } catch (Exception e) {
            // If the image path is invalid, use the default image
            Image defaultImage = new Image(DEFAULT_PHOTO_PATH);
            imageView.setImage(defaultImage);
        }
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

    private void handleSave(Article article, TextField nomField, TextField descriptionField,
                            TextField prixUnitaireField, ComboBox<Stock> stockCombo,
                            ComboBox<Fournisseur> fournisseurCombo, ComboBox<EtapeProjet> etapeProjetCombo,
                            ImageView photoView) {
        try {
            boolean changed = false;

            // Check for changes in fields
            if (!article.getNom().equals(nomField.getText())) {
                article.setNom(nomField.getText());
                changed = true;
            }

            if (!article.getDescription().equals(descriptionField.getText())) {
                article.setDescription(descriptionField.getText());
                changed = true;
            }

            if (!article.getPrixUnitaire().equals(prixUnitaireField.getText())) {
                article.setPrixUnitaire(prixUnitaireField.getText());
                changed = true;
            }

            if (stockCombo.getValue() != null && article.getStockId() != stockCombo.getValue().getId()) {
                logChange("Stock", article.getNom(), findStockById(stockDAO.getAllStocks(), article.getStockId()), stockCombo.getValue());
                article.setStockId(stockCombo.getValue().getId());
                changed = true;
            }

            if (fournisseurCombo.getValue() != null && article.getFournisseurId() != fournisseurCombo.getValue().getId()) {
                logChange("Fournisseur", article.getNom(), findFournisseurById(fournisseurDAO.getAllFournisseurs(), article.getFournisseurId()), fournisseurCombo.getValue());
                article.setFournisseurId(fournisseurCombo.getValue().getId());
                changed = true;
            }

            if (etapeProjetCombo.getValue() != null && article.getEtapeProjetId() != etapeProjetCombo.getValue().getId_etapeProjet()) {
                logChange("EtapeProjet", article.getNom(), findEtapeProjetById(etapeProjetDAO.getAllEtapeProjets(), article.getEtapeProjetId()), etapeProjetCombo.getValue());
                article.setEtapeProjetId(etapeProjetCombo.getValue().getId_etapeProjet());
                changed = true;
            }

            // Handle photo update
            String newPhotoPath = photoView.getImage().getUrl();
            if (!article.getPhoto().equals(newPhotoPath)) {
                article.setPhoto(newPhotoPath);
                changed = true; // Ensure the flag is set to true if only the photo is updated
            }

            // Save changes to the database
            if (changed) {
                if (articleDAO.updateArticle(article)) {
                    System.out.println("Successfully updated article: " + article.getId());
                } else {
                    System.out.println("Failed to update article: " + article.getId());
                }
            } else {
                System.out.println("No changes detected for article: " + article.getId());
            }
        } catch (Exception ex) {
            System.err.println("Error updating article: " + ex.getMessage());
            ex.printStackTrace();
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

    private Button createIconButton(String iconName, String tooltipText) {
        Button button = new Button();
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/" + iconName)));
            icon.setFitWidth(28);
            icon.setFitHeight(28);
            button.setGraphic(icon);
            button.setStyle("-fx-background-color: transparent;");
        } catch (Exception e) {
            System.err.println("Failed to load image: " + iconName);
            e.printStackTrace();
        }
        return button;
    }

    private void logChange(String attributeType, String articleName, Object oldValue, Object newValue) {
        File logFile = new File(LOG_FILE_NAME);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                String logEntry = String.format(
                        "[%s] Article '%s' has a new %s: '%s' -> '%s'%n",
                        LocalDateTime.now(), articleName, attributeType,
                        oldValue != null ? oldValue.toString() : "None",
                        newValue != null ? newValue.toString() : "None"
                );
                writer.write(logEntry);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    private void handlePhotoChange(ImageView photoView, Article article) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String newPhotoPath = selectedFile.toURI().toString();
            photoView.setImage(new Image(newPhotoPath));
            article.setPhoto(newPhotoPath); // Update the article's photo path
        }
    }

    public void PDF(ActionEvent actionEvent) {



        String pythonExe = "python"; // or "python3" depending on your setup

        // Path to the bot.py script
        String scriptPath = "C:/last version/OBatimaPi/src/main/resources/pdf.py"; // Adjust this path as needed

        // Create a process builder
        ProcessBuilder processBuilder = new ProcessBuilder(pythonExe, scriptPath);

        // Optional: Set working directory if necessary
        processBuilder.directory(new File("src/main/resources"));

        try {
            // Start the process
            Process process = processBuilder.start();

            // Optionally, you can handle the output and errors of the script
            // InputStream inputStream = process.getInputStream();
            // InputStream errorStream = process.getErrorStream();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}