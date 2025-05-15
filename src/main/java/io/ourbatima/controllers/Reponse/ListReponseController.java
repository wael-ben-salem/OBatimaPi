package io.ourbatima.controllers.Reponse;

import io.ourbatima.controllers.Reclamation.UpdateReclamationController;
import io.ourbatima.core.Dao.Reclamation.ReclamationDAO;
import io.ourbatima.core.Dao.Reclamation.ReponseDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.Reclamation;
import io.ourbatima.core.model.Reponse;
import io.ourbatima.core.ui.SimpleTranslationDialog;



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
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
public class ListReponseController extends ActionView {

    @FXML private GridPane reponseGrid;
    @FXML private HBox paginationButtonsBox;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private final ReponseDAO reponseDAO = new ReponseDAO();
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

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
        // Clear existing data rows (keep header rows 0 and 1)
        reponseGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 2; // Keep rows 0 and 1 (header and column titles)
        });

        // Calculate start and end indices for the current page
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allReponses.size());

        // Get reponses for the current page
        List<Reponse> currentPageReponses =
                (allReponses.isEmpty()) ? new ArrayList<>() :
                        allReponses.subList(startIndex, endIndex);

        // Add new data rows
        int rowIndex = 2; // Start from row 2 (after header and column titles)
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


            // Update Button with icon
            Button updateButton = new Button("✏️ Modifier");
            updateButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-font-size: 13px;");
            updateButton.setMinWidth(100);
            updateButton.setOnAction(e -> {
                System.out.println("Update button clicked for reponse ID: " + reponse.getId());
                openUpdatePopup(reponse.getId());
            });

            // Delete Button with icon
            Button deleteButton = new Button("🗑️ Supprimer");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-font-size: 13px;");
            deleteButton.setMinWidth(100);
            deleteButton.setOnAction(event -> {
                // Show confirmation dialog
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Confirmation de suppression");
                confirmDialog.setHeaderText(null);
                confirmDialog.setContentText("Voulez-vous vraiment supprimer cette réponse ?");

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    reponseDAO.deleteReponse(reponse.getId());
                    System.out.println("Reponse deleted successfully: " + reponse.getId());
                    loadReponses();
                }
            });

            // Show Button with icon
            Button showButton = new Button("👁️ Voir");
            showButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4; -fx-font-size: 13px;");
            showButton.setMinWidth(100);
            showButton.setOnAction(event -> {
                System.out.println("Show button clicked for reponse ID: " + reponse.getId());
                showReponseDetails(reponse);
            });

            // Create an HBox to hold the action buttons
            HBox actionButtons = new HBox(10); // 10 is the spacing between buttons
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.setPadding(new Insets(5));
            actionButtons.getChildren().addAll(showButton, updateButton, deleteButton);

            // Add components to the GridPane
            reponseGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField, ReponseIdField, actionButtons);
            rowIndex++;
        }

        System.out.println("Reponses loaded: " + allReponses.size());
    }



    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 12px;");
        return textField;
    }
    /**
     * Shows the details of a reponse in a dialog
     * @param reponse The reponse to show details for
     */
    private void showReponseDetails(Reponse reponse) {
        // Create a dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Détails de la Réponse");
        dialog.setHeaderText("Réponse #" + reponse.getId());

        // Create a grid for the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add details to the grid
        grid.add(new Label("ID:"), 0, 0);
        grid.add(new Label(String.valueOf(reponse.getId())), 1, 0);

        grid.add(new Label("Description:"), 0, 1);
        TextArea descriptionArea = new TextArea(reponse.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefWidth(400);
        descriptionArea.setPrefHeight(100);

        // Add translation button
        Button translateButton = new Button("🌎 Traduire");
        translateButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
        translateButton.setOnAction(event -> {
            SimpleTranslationDialog translationDialog = new SimpleTranslationDialog(reponse.getDescription());
            translationDialog.showAndWait();
        });

        // Create an HBox to hold the description and translate button
        HBox descriptionBox = new HBox(10);
        descriptionBox.setAlignment(Pos.CENTER_LEFT);
        descriptionBox.getChildren().addAll(descriptionArea, translateButton);

        grid.add(descriptionBox, 1, 1);

        grid.add(new Label("Statut:"), 0, 2);
        grid.add(new Label(reponse.getStatut()), 1, 2);

        grid.add(new Label("Date:"), 0, 3);
        grid.add(new Label(reponse.getDate().toString()), 1, 3);

        grid.add(new Label("ID Réclamation:"), 0, 4);
        grid.add(new Label(String.valueOf(reponse.getIdReclamation())), 1, 4);

        // Try to get the associated reclamation
        Reclamation reclamation = reclamationDAO.getReclamationById(reponse.getIdReclamation());
        if (reclamation != null) {
            grid.add(new Label("Détails de la réclamation:"), 0, 5);

            TitledPane reclamationPane = new TitledPane();
            reclamationPane.setText("Réclamation #" + reclamation.getId() + " - " + reclamation.getStatut());

            VBox reclamationContent = new VBox(5);
            reclamationContent.getChildren().addAll(
                    new Label("Date: " + reclamation.getDate()),
                    new Label("Description: " + reclamation.getDescription())
            );

            reclamationPane.setContent(reclamationContent);
            grid.add(reclamationPane, 1, 5);
        }

        // Set the dialog content
        dialog.getDialogPane().setContent(grid);

        // Add OK button
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        // Show the dialog
        dialog.showAndWait();
    }

    private void openUpdatePopup(int id) {
        try {
            System.out.println("Opening update popup for reponse ID: " + id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reponse/UpdateReponse.fxml"));
            Parent root = loader.load();

            UpdateReponseController updateReponseController = loader.getController();

            // Get the reponse by ID and pass it to the controller
            Reponse reponse = reponseDAO.getReponseById(id);
            if (reponse != null) {
                System.out.println("Found reponse: " + reponse.getId() + " - " + reponse.getDescription());
                updateReponseController.setReponseToUpdate(reponse);
            } else {
                System.out.println("Error: Could not find reponse with ID " + id);
                return;
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier la Réponse");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(event -> {
                System.out.println("Update popup closed, refreshing list");
                loadReponses();
            });

            System.out.println("Showing update popup");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening update popup: " + e.getMessage());
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
                pageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 50%;");
            } else {
                pageButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #495057; -fx-border-color: #dee2e6; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-padding: 5 10;");
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
            System.out.println("Opening AddReponse view");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reponse/AddReponse.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une réponse");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(e -> {
                System.out.println("AddReponse view closed, refreshing list");
                loadReponses();
            });

            System.out.println("Showing AddReponse view");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening AddReponse view: " + e.getMessage());
        }
    }
}