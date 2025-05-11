package io.OurBatima.controllers.Reponse;

import io.OurBatima.controllers.Reclamation.UpdateReclamationController;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reponse;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
public class ListReponseController extends ActionView {

    @FXML private GridPane reponseGrid;
    @FXML private HBox paginationButtonsBox;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private final ReponseDAO reponseDAO = new ReponseDAO();

    // Pagination variables
    private int currentPage = 1;
    private int itemsPerPage = 5;
    private int totalPages = 1;
    private List<Reponse> allReponses;

    @FXML
    public void initialize() {
        System.out.println("ListReponseController Initialized");
        loadReponses();
    }

    @FXML
    private void loadReponses() {
        // Get all reponses
        allReponses = reponseDAO.getAllReponses();

        // Calculate total pages
        totalPages = (int) Math.ceil((double) allReponses.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1; // At least one page even if empty

        // Ensure current page is valid
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        // Update pagination controls
        updatePaginationControls();

        // Display current page
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        // Clear existing data rows (keep header row 0)
        reponseGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Calculate start and end indices for the current page
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allReponses.size());

        // Get reponses for the current page
        List<Reponse> currentPageReponses =
            (allReponses.isEmpty()) ? new ArrayList<>() :
            allReponses.subList(startIndex, endIndex);

        // Add new data rows
        int rowIndex = 1;
        for (Reponse reponse : currentPageReponses) {
            // ID (non-editable)
            TextField idField = createTextField(String.valueOf(reponse.getId()));
            idField.setEditable(false);

            // Description (editable)
            TextField descriptionField = createTextField(reponse.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reponse.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reponse.getDate().toString());
            dateField.setEditable(false);

            TextField ReponseIdField = createTextField(String.valueOf(reponse.getIdReclamation()));
            ReponseIdField.setEditable(false);


            // Save Button
            Button saveButton = new Button("Update");
            saveButton.setOnAction(e -> openUpdatePopup(reponse.getId()));

            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                reponseDAO.deleteReponse(reponse.getId());
                System.out.println("Reponse deleted successfully: " + reponse.getId());
                loadReponses();
            });

            // Add components to the GridPane
            reponseGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField,ReponseIdField, saveButton, deleteButton);
            rowIndex++;
        }

        System.out.println("Reponses loaded: " + allReponses.size());
    }



    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
    }
    private void openUpdatePopup(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reponse/UpdateReponse.fxml"));
            Parent root = loader.load();

            UpdateReponseController UpdateReponseController = loader.getController();
             //updateReclamationController.setId(int id);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Reclamation");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField searchField; // Champ de texte pour entrer l'ID ou le critère de recherche

    @FXML
    private Label resultLabel;

    @FXML
    private void searchReponse() {
        String searchInput = searchField.getText().trim();

        if (searchInput.isEmpty()) {
            resultLabel.setText("Veuillez entrer un critère de recherche.");
            return;
        }

        try {
            // Recherche par ID (supposons que l'entrée est un ID)
            int id = Integer.parseInt(searchInput);
            Reponse reponse = reponseDAO.getReponseById(id);

            if (reponse != null) {
                // Afficher les détails dans le Label
                String result = String.format("ID: %d\nDescription: %s\nStatut: %s\nDate: %s\nID Réclamation: %d",
                        reponse.getId(),
                        reponse.getDescription(),
                        reponse.getStatut(),
                        reponse.getDate().toString(),
                        reponse.getIdReclamation());
                resultLabel.setText(result);

                // Mettre à jour la liste pour n'afficher que ce résultat
                allReponses = new ArrayList<>();
                allReponses.add(reponse);
                currentPage = 1;
                totalPages = 1;
                updatePaginationControls();
                displayCurrentPage();
            } else {
                resultLabel.setText("Aucune réponse trouvée pour l'ID " + id);
            }
        } catch (NumberFormatException e) {
            // Si l'entrée n'est pas un nombre, rechercher par description
            List<Reponse> searchResults = reponseDAO.getAllReponses().stream()
                    .filter(r -> r.getDescription().toLowerCase().contains(searchInput.toLowerCase()))
                    .collect(Collectors.toList());

            if (!searchResults.isEmpty()) {
                // Afficher le nombre de résultats trouvés
                resultLabel.setText(searchResults.size() + " réponse(s) trouvée(s) contenant '" + searchInput + "'.");

                // Mettre à jour la liste pour afficher les résultats de recherche avec pagination
                allReponses = searchResults;
                currentPage = 1;
                totalPages = (int) Math.ceil((double) allReponses.size() / itemsPerPage);
                updatePaginationControls();
                displayCurrentPage();
            } else {
                resultLabel.setText("Aucune réponse trouvée pour '" + searchInput + "'");
            }
        }
    }

    /**
     * Updates the pagination controls based on the current state
     */
    private void updatePaginationControls() {
        // Update page info label
        pageInfoLabel.setText("Page " + currentPage + " of " + totalPages);

        // Enable/disable previous and next buttons
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(currentPage >= totalPages);

        // Clear existing page number buttons
        paginationButtonsBox.getChildren().clear();

        // Add page number buttons
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + 4);

        // Adjust start page if we're near the end
        if (endPage - startPage < 4 && startPage > 1) {
            startPage = Math.max(1, endPage - 4);
        }

        for (int i = startPage; i <= endPage; i++) {
            Button pageButton = new Button(String.valueOf(i));
            final int pageNum = i;

            // Style the current page button differently
            if (i == currentPage) {
                pageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            pageButton.setOnAction(event -> {
                currentPage = pageNum;
                displayCurrentPage();
                updatePaginationControls();
            });

            paginationButtonsBox.getChildren().add(pageButton);
        }
    }

    /**
     * Go to the previous page
     */
    @FXML
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Go to the next page
     */
    @FXML
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Opens the AddReponse view when the "Ajouter une réponse" button is clicked
     */
    @FXML
    private void openAddReponseView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reponse/AddReponse.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une réponse");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(e -> loadReponses());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening AddReponse view: " + e.getMessage());
        }
    }
}


