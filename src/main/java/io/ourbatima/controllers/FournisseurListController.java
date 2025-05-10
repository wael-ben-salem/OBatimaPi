package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.Stock.FournisseurDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Fournisseur;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.List;

public class FournisseurListController extends ActionView {

    @FXML private GridPane fournisseurGrid;
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    public void onInit(Context context) {
        super.onInit(context);
        loadFournisseurs(); // Automatically load fournisseurs on initialization
    }

    @FXML
    private void loadFournisseurs() {
        List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();

        // Clear existing data rows (keep header row 0)
        fournisseurGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Add new data rows
        int rowIndex = 1;
        for (Fournisseur fournisseur : fournisseurs) {
            // Nom (editable)
            TextField nomField = createTextField(fournisseur.getNom());

            // Prenom (editable)
            TextField prenomField = createTextField(fournisseur.getPrenom());

            // Email (editable)
            TextField emailField = createTextField(fournisseur.getEmail());

            // Numero de Telephone (editable)
            TextField numeroDeTelephoneField = createTextField(fournisseur.getNumeroDeTelephone());

            // Adresse (editable)
            TextField adresseField = createTextField(fournisseur.getAdresse());

            // Save Button with icon
            Button saveButton = createIconButton("pencil.png", "Save");
            saveButton.setOnAction(event -> {
                // Update the fournisseur object
                fournisseur.setNom(nomField.getText());
                fournisseur.setPrenom(prenomField.getText());
                fournisseur.setEmail(emailField.getText());
                fournisseur.setNumeroDeTelephone(numeroDeTelephoneField.getText());
                fournisseur.setAdresse(adresseField.getText());

                // Save to database
                boolean success = fournisseurDAO.updateFournisseur(fournisseur);
                if (success) {
                    System.out.println("Fournisseur updated successfully: " + fournisseur.getId());
                } else {
                    System.out.println("Failed to update fournisseur: " + fournisseur.getId());
                }
            });

            // Delete Button with icon
            Button deleteButton = createIconButton("bin.png", "Delete");
            deleteButton.setStyle("-fx-background-color: transparent;"); // Ensure icon is visible
            deleteButton.setOnAction(event -> {
                // Delete the fournisseur from the database
                boolean success = fournisseurDAO.deleteFournisseur(fournisseur.getId());
                if (success) {
                    System.out.println("Fournisseur deleted successfully: " + fournisseur.getId());
                    // Reload the fournisseurs after deletion
                    loadFournisseurs();
                } else {
                    System.out.println("Failed to delete fournisseur: " + fournisseur.getId());
                }
            });

            // Add components to the GridPane
            fournisseurGrid.addRow(rowIndex, nomField, prenomField, emailField, numeroDeTelephoneField, adresseField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Fournisseurs loaded: " + fournisseurs.size());
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
            button.setTooltip(new Tooltip(tooltipText)); // Set tooltip for the button
        } catch (Exception e) {
            System.err.println("Failed to load image: " + iconName);
            e.printStackTrace();
        }
        return button;
    }
}