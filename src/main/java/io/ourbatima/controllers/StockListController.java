package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.Stock.StockDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Stock;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class StockListController extends ActionView {

    @FXML private GridPane stockGrid;
    private final StockDAO stockDAO = new StockDAO();

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        loadStocks(); // Automatically load stocks on initialization
    }

    @FXML
    private void loadStocks() {
        List<Stock> stocks = stockDAO.getAllStocks();

        // Clear existing data rows (keep header row 0)
        stockGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Add new data rows
        int rowIndex = 1;
        for (Stock stock : stocks) {
            // Nom (editable)
            TextField nomField = createTextField(stock.getNom());

            // Emplacement (editable)
            TextField emplacementField = createTextField(stock.getEmplacement());

            // Date Creation (editable)
            TextField dateCreationField = createTextField(stock.getDateCreation());

            // Save Button with icon
            Button saveButton = createIconButton("pencil.png", "Save");
            saveButton.setOnAction(event -> {
                // Update the stock object
                stock.setNom(nomField.getText());
                stock.setEmplacement(emplacementField.getText());
                stock.setDateCreation(dateCreationField.getText());

                // Save to database
                boolean success = stockDAO.updateStock(stock);
                if (success) {
                    System.out.println("Stock updated successfully: " + stock.getId());
                } else {
                    System.out.println("Failed to update stock: " + stock.getId());
                }
            });

            // Delete Button with icon
            Button deleteButton = createIconButton("bin.png", "Delete");
            deleteButton.setStyle("-fx-background-color: transparent;"); // Ensure icon is visible
            deleteButton.setOnAction(event -> {
                // Delete the stock from the database
                boolean success = stockDAO.deleteStock(stock.getId());
                if (success) {
                    System.out.println("Stock deleted successfully: " + stock.getId());
                    // Reload the stocks after deletion
                    loadStocks();
                } else {
                    System.out.println("Failed to delete stock: " + stock.getId());
                }
            });

            // Add components to the GridPane
            stockGrid.addRow(rowIndex, nomField, emplacementField, dateCreationField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Stocks loaded: " + stocks.size());
    }

    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
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
}