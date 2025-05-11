package io.OurBatima.controllers.Reclamation;


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/*public class ListReclamationController extends ActionView {

    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colDescription;
    @FXML private TableColumn<Reclamation, String> colStatut;
    @FXML private TableColumn<Reclamation, Timestamp> colDate;
    @FXML private TableColumn<Reclamation, String> colActions;


    private ObservableList<Reclamation> reclamationData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("AfficherReclamation Controller Initialized");
        setupTable();
        loadReclamations();

      /*  searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchInput();
            if (newValue.isEmpty()) {
                suggestionsList.setVisible(false);
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
                searchField.setText(selectedSuggestion);
                suggestionsList.setVisible(false);
                handleSearch();
            }
        });
    }

    @FXML
   /* private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<Reclamation> filteredList = reclamationData.stream()
                .filter(reclamation -> reclamation.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        reclamationTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    private void handleSearchInput() {
        String searchText = searchField.getText().toLowerCase();
        List<String> suggestions = reclamationData.stream()
                .map(Reclamation::getDescription)
                .filter(desc -> desc.toLowerCase().startsWith(searchText))
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            suggestionsList.getItems().setAll(suggestions);
            suggestionsList.setVisible(true);
        } else {
            suggestionsList.setVisible(false);
        }
    }
*/
  /*  private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colActions.setCellFactory(new Callback<TableColumn<Reclamation, String>, TableCell<Reclamation, String>>() {
            @Override
            public TableCell<Reclamation, String> call(TableColumn<Reclamation, String> param) {
                return new TableCell<Reclamation, String>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    {
                        updateButton.setStyle("-fx-background-color: #b39427; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #4c3f0a; -fx-text-fill: white;");
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            updateButton.setOnAction(event -> {
                                Reclamation reclamation = getTableView().getItems().get(getIndex());
                               // openUpdateReclamationWindow(reclamation);
                            });

                            deleteButton.setOnAction(event -> {
                                Reclamation reclamation = getTableView().getItems().get(getIndex());
                                deleteReclamation(reclamation.getId());
                            });

                            HBox hBox = new HBox(10, updateButton, deleteButton);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

   /* private void openUpdateReclamationWindow(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();
            UpdateReclamation controller = loader.getController();
            controller.initData(reclamation, this);

            Stage stage = new Stage();
            stage.setTitle("Update Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre de modification.");
            alert.showAndWait();
        }
    }*/
/*
    @FXML
    public void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
            System.out.println("Fetched Reclamations: " + reclamations);
            reclamationData.setAll(reclamations);

            reclamationTable.layout();
            reclamationTable.setItems(reclamationData);
            reclamationTable.refresh();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Échec du chargement des réclamations : " + e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }*/
/*
    private void deleteReclamation(int reclamationId) {
        reclamationDAO.deleteReclamation(reclamationId);
        reclamationData.removeIf(r -> r.getId() == reclamationId);
        reclamationTable.setItems(reclamationData);
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        loadReclamations();
        initialize();
   }
}*/


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;

import static java.awt.SystemColor.text;
import io.OurBatima.core.Dao.Reclamation.ReponseDAO;
import io.OurBatima.core.model.Reponse;

public class ListReclamationController extends ActionView {

    @FXML private GridPane reclamationGrid;
    @FXML private HBox paginationButtonsBox;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;

    private final ReclamationDAO reclamationDAO = new ReclamationDAO();
    private final ReponseDAO reponseDAO = new ReponseDAO();

    // Pagination variables
    private int currentPage = 1;
    private int itemsPerPage = 5;
    private int totalPages = 1;
    private List<Reclamation> allReclamations;

    @FXML
    public void initialize() {
        System.out.println("ListReclamationController Initialized");
        loadReclamations();
    }


    @FXML
    private void loadReclamations() {
        // Get all reclamations
        allReclamations = reclamationDAO.getAllReclamations();

        // Calculate total pages
        totalPages = (int) Math.ceil((double) allReclamations.size() / itemsPerPage);
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
        reclamationGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Calculate start and end indices for the current page
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allReclamations.size());

        // Get reclamations for the current page
        List<Reclamation> currentPageReclamations =
            (allReclamations.isEmpty()) ? new ArrayList<>() :
            allReclamations.subList(startIndex, endIndex);

        // Add new data rows
        int rowIndex = 1;
        for (Reclamation reclamation : currentPageReclamations) {
            // ID (non-editable)
            TextField idField = createTextField(String.valueOf(reclamation.getId()));
            idField.setEditable(false);

            // Description (editable)
            TextField descriptionField = createTextField(reclamation.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reclamation.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reclamation.getDate().toString());
            dateField.setEditable(false);

           /* ComboBox<Integer> utilisateurComboBox = new ComboBox<>();
            utilisateurComboBox.getItems().addAll(getAllUtilisateurIds()); // Method to fetch all user IDs
            utilisateurComboBox.setValue(reclamation.getUtilisateurId().toString()); // Set the current user ID
            utilisateurComboBox.setDisable(true); // Make it non-editable*/

            TextField UtilisateurIdField = createTextField(String.valueOf(reclamation.getUtilisateurId()));
            dateField.setEditable(false);


            // Save Button
            Button saveButton = new Button("update");
            saveButton.setOnAction(e -> openUpdatePopup(reclamation.getId()));



            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                handleDeleteReclamation(reclamation.getId());
            });

            // Add components to the GridPane
            reclamationGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField,UtilisateurIdField , saveButton, deleteButton);
            rowIndex++;
        }
    }

    private void openUpdatePopup(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();

            UpdateReclamationController updateReclamationController = loader.getController();
            updateReclamationController.setReclamationId(id); // Pass the reclamation ID to the controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Reclamation");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(event -> loadReclamations());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening update popup: " + e.getMessage());
        }
    }


    @FXML
    private void exportToPdf() {
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
        if (reclamations.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Aucune réclamation à exporter.");
            return;
        }

        // Ouvrir une boîte de dialogue pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file == null) {
            return; // L'utilisateur a annulé
        }

        // S'assurer que le fichier a l'extension .pdf
        String filePath = file.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
            file = new File(filePath);
        }

        try (PDDocument document = new PDDocument()) {
            // Créer la première page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Obtenir les dimensions de la page
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            // Marges
            float margin = 50;
            float yStart = pageHeight - margin;
            float tableWidth = pageWidth - 2 * margin;
            float yPosition = yStart;

            // Largeurs des colonnes
            float[] columnWidths = {40, tableWidth * 0.4f, tableWidth * 0.2f, tableWidth * 0.25f, tableWidth * 0.15f};
            float rowHeight = 20f;

            // Nombre de pages
            int pageNumber = 1;

            // Créer le contenu
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Ajouter un en-tête avec logo ou titre
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Liste des Réclamations");
            contentStream.endText();
            yPosition -= 25;

            // Ajouter la date d'exportation
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Exporté le: " + java.time.LocalDate.now().toString());
            contentStream.endText();
            yPosition -= 30;

            // Dessiner l'en-tête du tableau
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Fond de l'en-tête
            contentStream.setNonStrokingColor(0.8f, 0.8f, 0.8f); // Gris clair
            contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(0, 0, 0); // Retour au noir

            // Bordure de l'en-tête
            contentStream.setLineWidth(0.5f);
            contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
            contentStream.stroke();

            // Texte de l'en-tête
            String[] headers = {"ID", "Description", "Statut", "Date", "Utilisateur"};
            float xPosition = margin;

            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(headers[i]);
                contentStream.endText();

                // Dessiner les lignes verticales des colonnes
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();

                xPosition += columnWidths[i];
            }

            // Dernière ligne verticale
            contentStream.moveTo(xPosition, yPosition);
            contentStream.lineTo(xPosition, yPosition - rowHeight);
            contentStream.stroke();

            yPosition -= rowHeight;
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            // Parcourir la liste des réclamations
            for (Reclamation reclamation : reclamations) {
                // Vérifier s'il faut créer une nouvelle page
                if (yPosition < margin + rowHeight) {
                    // Ajouter le numéro de page
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(pageWidth / 2, margin / 2);
                    contentStream.showText("Page " + pageNumber);
                    contentStream.endText();

                    // Fermer le contentStream actuel
                    contentStream.close();

                    // Créer une nouvelle page
                    pageNumber++;
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = yStart;

                    // Réécrire l'en-tête sur la nouvelle page
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Liste des Réclamations (suite)");
                    contentStream.endText();
                    yPosition -= 25;

                    // Ajouter la date d'exportation
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Exporté le: " + java.time.LocalDate.now().toString());
                    contentStream.endText();
                    yPosition -= 30;

                    // Redessiner l'en-tête du tableau
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                    // Fond de l'en-tête
                    contentStream.setNonStrokingColor(0.8f, 0.8f, 0.8f);
                    contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
                    contentStream.fill();
                    contentStream.setNonStrokingColor(0, 0, 0);

                    // Bordure de l'en-tête
                    contentStream.setLineWidth(0.5f);
                    contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
                    contentStream.stroke();

                    // Texte de l'en-tête
                    xPosition = margin;
                    for (int i = 0; i < headers.length; i++) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                        contentStream.showText(headers[i]);
                        contentStream.endText();

                        contentStream.moveTo(xPosition, yPosition);
                        contentStream.lineTo(xPosition, yPosition - rowHeight);
                        contentStream.stroke();

                        xPosition += columnWidths[i];
                    }

                    // Dernière ligne verticale
                    contentStream.moveTo(xPosition, yPosition);
                    contentStream.lineTo(xPosition, yPosition - rowHeight);
                    contentStream.stroke();

                    yPosition -= rowHeight;
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                }

                // Dessiner la ligne du tableau pour cette réclamation
                contentStream.setLineWidth(0.5f);
                contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
                contentStream.stroke();

                // Ajouter les données de la réclamation
                xPosition = margin;

                // ID
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(String.valueOf(reclamation.getId()));
                contentStream.endText();
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();
                xPosition += columnWidths[0];

                // Description (tronquée si trop longue)
                String description = reclamation.getDescription();
                if (description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(description);
                contentStream.endText();
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();
                xPosition += columnWidths[1];

                // Statut
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(reclamation.getStatut());
                contentStream.endText();
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();
                xPosition += columnWidths[2];

                // Date (formatée)
                String formattedDate = reclamation.getDate().toLocalDate().toString();
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(formattedDate);
                contentStream.endText();
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();
                xPosition += columnWidths[3];

                // Utilisateur ID
                contentStream.beginText();
                contentStream.newLineAtOffset(xPosition + 5, yPosition - 15);
                contentStream.showText(String.valueOf(reclamation.getUtilisateurId()));
                contentStream.endText();
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();
                xPosition += columnWidths[4];

                // Dernière ligne verticale
                contentStream.moveTo(xPosition, yPosition);
                contentStream.lineTo(xPosition, yPosition - rowHeight);
                contentStream.stroke();

                yPosition -= rowHeight;
            }

            // Ajouter le numéro de page sur la dernière page
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(pageWidth / 2, margin / 2);
            contentStream.showText("Page " + pageNumber);
            contentStream.endText();

            // Fermer le dernier contentStream
            contentStream.close();

            // Sauvegarder le document
            document.save(file);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le PDF a été créé avec succès.\n" +
                      "Emplacement: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du PDF: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
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
     * Opens the AddReclamation view when the "Ajouter une réclamation" button is clicked
     */
    @FXML
    private void openAddReclamationView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reclamation/AddReclamation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une réclamation");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(e -> loadReclamations());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening AddReclamation view: " + e.getMessage());
        }
    }

    /**
     * Handles the deletion of a reclamation with proper confirmation and error handling
     * @param reclamationId The ID of the reclamation to delete
     */
    private void handleDeleteReclamation(int reclamationId) {
        try {
            // Check if there are any responses associated with this reclamation
            List<Reponse> responses = reponseDAO.getReponsesByReclamationId(reclamationId);

            // Create confirmation dialog
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText(null);

            if (!responses.isEmpty()) {
                // Warn about associated responses
                confirmDialog.setContentText("Cette réclamation a " + responses.size() + " réponse(s) associée(s). \n" +
                                           "La suppression de cette réclamation supprimera également toutes les réponses associées. \n\n" +
                                           "Voulez-vous vraiment supprimer cette réclamation ?");
            } else {
                confirmDialog.setContentText("Voulez-vous vraiment supprimer cette réclamation ?");
            }

            // Show dialog and wait for user response
            Optional<ButtonType> result = confirmDialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed deletion
                try {
                    // First delete associated responses if any
                    for (Reponse response : responses) {
                        reponseDAO.deleteReponse(response.getId());
                    }

                    // Then delete the reclamation
                    reclamationDAO.deleteReclamation(reclamationId);

                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès.");

                    // Refresh the list
                    loadReclamations();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la vérification des réponses associées: " + e.getMessage());
        }
    }
}
